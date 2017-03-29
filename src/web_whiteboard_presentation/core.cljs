(ns web-whiteboard-presentation.core
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [sablono.core :as sablono :refer-macros [html]]
            [goog.events :as events])
  (:import [goog.events EventType KeyHandler KeyCodes]))

(enable-console-print!)

(println "This text is printed from src/web-whiteboard-presentation/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(declare next-slide)
(declare prev-slide)
(declare event->key-binding)

(defn Slide
  [slide-name children]
  (q/component
   (fn [{:keys [slides slide-index]} ui-state]
     (sablono/html [:div.slide
                    [:div.header (str slide-name)]
                    children
                    [:div.nav_container
                     [:div.nav-prev.nav_button
                      {:on-click #(prev-slide)}
                      "<-"]
                     [:div.nav-next.nav_button
                      {:on-click #(next-slide)}
                      "->"]]
                    [:div.footer
                     (str slide-name)
                     [:div (str (inc slide-index) "/" (count slides))]]]))
   {:name (str "Slide: " (name slide-name))}))

(def Presentation
  (q/component
   (fn [{:keys [slides slide-index] :as ui-state}]
     (let [slide (nth slides slide-index)]
       (sablono/html [:div
                      {:on-keyup #(.log js/console (str (event->key-binding %)))}
                      [:div.title "Presentation Title"]
                      (slide ui-state)])))
   {:name "Presentation"}))

(def slides
  [(Slide :title [:div "This is the title: web-whiteboard"])
   (Slide :intro [:div.header "Slide Header: This is the intro slide"])
   (Slide :purpose [:div "This is the purpose slide"])
   (Slide :assumed-knowledge [:div "This is the assumed knowledge"])
   (Slide :summary [:div "This will be the summary"])
   (Slide :user-interface [:div "This is the user interface"])
   (Slide :questions? [:div "Do you have any questions?"])])

(def ui-atom (atom {:slides slides
                    :slide-index 0}))

(defn move-slide
  [step-fn]
  (let [{:keys [slides slide-index]} @ui-atom
        idx (mod (step-fn slide-index) (count slides))]
    (swap! ui-atom assoc :slide-index idx)))

(defn next-slide
  []
  (move-slide inc))

(defn prev-slide
  []
  (move-slide dec))

(defn render
  [_key atom _old-state new-state]
  (q/render (Presentation new-state)
            (.getElementById js/document "app")))

(defn event->key-binding
  "Determines the key-binding, based on a DOM event"
  [e]
  (reduce (fn [acc [include? k]]
            (if include?
              (conj acc k)
              acc))
          #{(.-keyCode e)}
          (map vector
               [e.ctrlKey e.shiftKey]
               [KeyCodes.CTRL KeyCodes.SHIFT])))

(def keyboard-data-definitions
  [{:doc "Move to the next slide"
    :key-binding #{KeyCodes.F}
    :command-name "Next"
    :handler next-slide}
   {:doc "Move to the previous slide"
    :key-binding #{KeyCodes.B}
    :command-name "Prev"
    :handler prev-slide}
   {:key-binding #{KeyCodes.LEFT}
    :handler prev-slide}
   {:key-binding #{KeyCodes.RIGHT}
    :handler next-slide}])

(def keybinding-handlers
  "keybinding-handlers for the app"
  (reduce (fn [acc {:keys [key-binding handler]}]
            (assoc acc
                   key-binding
                   handler))
          {}
          keyboard-data-definitions))

(defn keybinding-dispatcher
  "Routes a key-binding to the proper keybinding-handler function"
  [event key-binding]
  (when-let [handler (keybinding-handlers key-binding)]
    (handler)))

(defn keybinding-event-handler
  "Determines key-binding, based on a DOM event, and calls keybinding-dispatcher with it"
  [e]
  (let [key-binding (event->key-binding e)]
    (keybinding-dispatcher e key-binding)))

(defn listen-to-keybindings
  "Register the user interface to listen for keybinding events"
  []
  (let [kh (KeyHandler. js/document)]
    (events/listen kh
                   KeyHandler.EventType.KEY
                   keybinding-event-handler)))

(defn start-app
  []
  (listen-to-keybindings)
  (add-watch ui-atom :watch-render render)
  (q/render (Presentation @ui-atom)
            (.getElementById js/document "app")))

(start-app)
