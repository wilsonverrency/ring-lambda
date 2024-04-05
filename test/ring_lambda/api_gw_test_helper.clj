(ns ring-lambda.api-gw-test-helper
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring.util.codec :as codec]))

(defn gen-event [{:keys [path-prefix path-params proxy-key method query-params] :as opts}]
  (let [resource (format "%s/{%s+}" path-prefix proxy-key)
        path (format "%s/%s" path-prefix path-params)
        sample (json/parse-string (slurp (io/resource "sample-apigw-event.json")) true)]
    (-> sample
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
                                         :httpMethod method))))))

(comment
  (-> {:path-prefix "/foo"
       :path-params "math/plus"
       :proxy-key "proxy"
       :method "GET"
       :query-params (codec/form-decode "x=2&y=4" "UTF-8")}
      (gen-event)
      (json/generate-string)))