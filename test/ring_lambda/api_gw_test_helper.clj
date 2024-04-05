(ns ring-lambda.api-gw-test-helper
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring.util.codec :as codec]))

(defn gen-event [{:keys [path-prefix path-params proxy-key method query-string body] :as opts}]
  (let [resource (format "%s/{%s+}" path-prefix proxy-key)
        path (format "%s/%s" path-prefix path-params)
        query-params (codec/form-decode query-string "UTF-8")
        sample (-> (io/resource "sample-apigw-event.json")
                   (slurp)
                   (json/parse-string true)
                   (assoc :resource resource
                          :path path
                          :method method
                          :pathParameters {proxy-key path-params}
                          :queryStringParameters query-params
                          :multiValueQueryStringParameters (update-vals query-params
                                                                        (fn [v]
                                                                          (if (vector? v)
                                                                            v
                                                                            [v]))))
                   (update :requestContext (fn [rc]
                                             (assoc rc
                                                    :resourcePath resource
                                                    :path resource
                                                    :httpMethod method))))]
    (if body
      (assoc sample :body body)
      (dissoc sample :body))))

(comment
  (-> {:path-prefix "/foo"
       :path-params "math/plus"
       :proxy-key "proxy"
       :method "GET"
       :query-string "x=2&y=4"}
      (gen-event)
      #_(json/generate-string)))