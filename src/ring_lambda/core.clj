(ns ring-lambda.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [ring-lambda.apigw :as apigw])
  (:gen-class
   :implements [com.amazonaws.services.lambda.runtime.RequestStreamHandler]))

(defn handler [request]
  {:status 200
   :body (json/generate-string request)})

(defn -handleRequest
  "Implementation returns a lambda proxy integration response"
  [_this in out _ctx]
  (let [event   (json/parse-stream (io/reader in :encoding "UTF-8") true)
        request (apigw/->ring-request event)]
    (with-open [w (io/writer out)]
      (json/generate-stream (apigw/->apigw-response (handler request)) w))))