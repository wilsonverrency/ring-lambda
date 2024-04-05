(ns ring-lambda.core-test
  (:require [cheshire.core :as json]
            [clojure.test :refer [deftest is]]
            [ring-lambda.core :refer [-handleRequest]]
            [ring-lambda.test-helpers :as test-helpers])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(deftest api-gw-handler-test
  (let [in  (-> {:method       "GET"
                 :resource     "/math/plus"
                 :query-string "x=1&y=2"}
                (test-helpers/api-gw-proxy-event)
                (json/generate-string)
                (.getBytes)
                (ByteArrayInputStream.))
        out (ByteArrayOutputStream.)
        _   (-handleRequest nil in out nil)
        {:keys [statusCode
                body
                headers]} (-> (.toString out)
                              (json/parse-string true))]
    (is (= 200 statusCode))
    (is (= {:total 3} (json/parse-string body true)))
    (is (= "application/json; charset=utf-8" (get headers :Content-Type)))))