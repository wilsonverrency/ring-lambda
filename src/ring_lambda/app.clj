(ns ring-lambda.app
  (:require [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [ring.middleware.params :as params]))

(defn routes []
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "my-api"}}
           :handler (swagger/create-swagger-handler)}}]

   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get {:summary "plus with spec query parameters"
            :parameters {:query {:x int?, :y int?}}
            :responses {200 {:body {:total int?}}}
            :handler (fn [{{{:keys [x y]} :query} :parameters}]
                       {:status 200
                        :body {:total (+  x y)}})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses {200 {:body {:total int?}}}
             :handler (fn [{{{:keys [x y]} :body} :parameters}]
                        {:status 200
                         :body {:total (+ x y)}})}}]]])

(defn ring-handler [routes]
  (ring/ring-handler
   (ring/router

    routes

    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [;; query-params & form-params
                         params/wrap-params
                             ;; content-negotiation
                         muuntaja/format-negotiate-middleware
                             ;; encoding response body
                         muuntaja/format-response-middleware
                             ;; exception handling
                         exception/exception-middleware
                             ;; decoding request body
                         muuntaja/format-request-middleware
                             ;; coercing response bodys
                         coercion/coerce-response-middleware
                             ;; coercing request parameters
                         coercion/coerce-request-middleware
                             ;; TODO: does not handle multipart!
                         #_multipart/multipart-middleware]}})
   (ring/routes
    (ring/create-default-handler))))