(ns ring-lambda.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [reitit.coercion.spec]
            [ring-lambda.app :as app]
            [ring-lambda.api-gw :as api-gw])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn -handleRequest
  "Implementation returns a lambda proxy integration response"
  [_this in out ctx]
  (let [event   (json/parse-stream (io/reader in :encoding "UTF-8") true)
        proxy-key (or (some-> (System/getenv "RESOURCE_PROXY") keyword) :proxy)
        request (api-gw/->ring-request event ctx proxy-key)
        handler (app/ring-handler (app/routes))]
    (with-open [w (io/writer out)]
      (json/generate-stream (api-gw/->api-gw-response (handler request)) w))))