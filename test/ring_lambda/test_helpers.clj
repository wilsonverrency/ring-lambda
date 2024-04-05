(ns ring-lambda.test-helpers
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [ring.util.codec :as codec])
  (:import [java.time Instant]))

(defn resource-path [resource path-params]
  (reduce (fn replace-path-param [path [k v]]
            (let [proxy-param (str "{" k "+}")
                  param (str "{" k "}")]
              (if (s/includes? path proxy-param)
                (s/replace path proxy-param v)
                (s/replace path param v))))
          resource
          path-params))

(defn api-gw-proxy-event
  [{:keys [resource path-params method query-string body request-time-epoch request-id]
    :or {request-id         (.toString (random-uuid))
         request-time-epoch (System/currentTimeMillis)}}]
  (let [path         (resource-path resource path-params)
        request-time (.toString (Instant/ofEpochMilli request-time-epoch))
        query-params (if (string? query-string)
                       (codec/form-decode query-string "UTF-8")
                       {})
        event        (-> (io/resource "sample-api-gw-event.json")
                         (slurp)
                         (json/parse-string true)
                         (assoc :path path
                                :resource resource
                                :httpMethod method
                                :pathParameters (or path-params {})
                                :queryStringParameters query-params
                                :multiValueQueryStringParameters (update-vals query-params
                                                                              (fn [v]
                                                                                (if (vector? v)
                                                                                  v
                                                                                  [v]))))
                         (update :requestContext (fn [rc]
                                                   (assoc rc
                                                          :path resource
                                                          :resourcePath resource
                                                          :httpMethod method
                                                          :requestId request-id
                                                          :requestTime request-time
                                                          :requestTimeEpoch request-time-epoch))))]

    (if body
      (assoc event :body body)
      (dissoc event :body))))

(comment
  (-> (api-gw-proxy-event {:resource "/accounts/{accountId}/{proxy+}"
                           :path-params {"accountId" "fred"
                                         "proxy" "math/plus"}
                           :method "GET"})
      #_(json/generate-string))

  (api-gw-proxy-event {:resource "/accounts/math/plus"
                       :method "GET"}))