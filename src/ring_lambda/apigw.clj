(ns ring-lambda.apigw
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [ring.util.codec :as codec])
  (:import [com.amazonaws.services.lambda.runtime RequestStreamHandler]
           [java.io InputStream File]
           [clojure.lang ISeq]))

(defn interpolate-path [params path]
  (reduce (fn -interpolate-path [acc [key value]]
            (s/replace acc
                       (re-pattern (format "\\{%s\\}" (name key)))
                       value))
          path
          params))

(defn ->ring-request
  "Transform lambda input to Ring requests. Has two extra properties:
   :event - the lambda input
   :context - an instance of a lambda context
              http://docs.aws.amazon.com/lambda/latest/dg/java-context-object.html"
  [event]
  (let [[http-version host]
        (s/split (get-in event [:headers :Via] "") #" ")]
    {:server-port
     (try
       (Integer/parseInt (get-in event [:headers :X-Forwarded-Port]))
       (catch NumberFormatException e nil))
     :body           (get event :body)
     :server-name    host
     :remote-addr    (get event :source-ip "")
     :uri            (get event :path) #_(interpolate-path (get event :path {})
                                                           (get event :resource-path ""))
     :query-string   (codec/form-encode (get event :query-string {}))
     :scheme         (keyword
                      (get-in event [:headers :X-Forwarded-Proto]))
     :request-method (keyword
                      (s/lower-case (get event :http-method "")))
     :protocol       (format "HTTP/%s" http-version)
     :headers        (into {} (map (fn -header-keys [[k v]]
                                     [(s/lower-case (name k)) v])
                                   (:headers event)))
     :event          event}))

(defmulti wrap-body class)
(defmethod wrap-body String [body] body)
(defmethod wrap-body ISeq [body] (s/join body))
(defmethod wrap-body File [body] (slurp body))
(defmethod wrap-body InputStream [body] (slurp body))

(defn ->apigw-response [response]
  {:statusCode      (:status response)
   :body            (wrap-body (:body response))
   :headers         (:headers response)})