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
                    ;[:div.header (str slide-name)]
                    [:div.content
                     children]
                    
                    ;; [:div.nav_container
                    ;;  [:div.nav-prev.nav_button
                    ;;   {:on-click #(prev-slide)}
                    ;;   "<-"]
                    ;;  [:div.nav-next.nav_button
                    ;;   {:on-click #(next-slide)}
                    ;;   "->"]]

                    [:div.footer
                     [:h3 (str slide-name)]
                     [:h3 (str (inc slide-index) "/" (count slides))]]]))
   {:name (str "Slide: " (name slide-name))}))

(def Presentation
  (q/component
   (fn [{:keys [slides slide-index] :as ui-state}]
     (let [slide (nth slides slide-index)]
       (sablono/html [:div
                      {:on-keyup #(.log js/console (str (event->key-binding %)))}
                      (slide ui-state)])))
   {:name "Presentation"}))

(def programming-history
  [{:year 2009
    :language-use {:python "XXXX"
                   :java "XXXX"
                   :c "-XX-"
                   :javascript "----"
                   :C# "----"
                   :haskell "----"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2010
    :language-use {:python "XXXX"
                   :java "XXXX"
                   :c "--X-"
                   :javascript "----"
                   :C# "----"
                   :haskell "----"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2011
    :language-use {:python "XXXX"
                   :java "XXXX"
                   :c "--X-"
                   :javascript "----"
                   :C# "----"
                   :haskell "----"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2012
    :language-use {:python "XXX-"
                   :java "XXX-"
                   :c "----"
                   :javascript "XXXX"
                   :C# "---X"
                   :haskell "----"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2013
    :language-use {:python "----"
                   :java "----"
                   :c "----"
                   :javascript "XXXX"
                   :C# "XXXX"
                   :haskell "--X-"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2014
    :language-use {:python "----"
                   :java "----"
                   :c "----"
                   :javascript "XXXX"
                   :C# "XXXX"
                   :haskell "--X-"
                   :elm "----"
                   :racket "----"
                   :clojure "----"}}
   {:year 2015
    :language-use {:python "-X-X"
                   :java "----"
                   :c "----"
                   :javascript "XXXX"
                   :C# "XXXX"
                   :haskell "----"
                   :elm "--XX"
                   :racket "-XX-"
                   :clojure "---X"}}
   {:year 2016
    :language-use {:python "----"
                   :java "****"
                   :c "-X-X"
                   :javascript "****"
                   :C# "-X--"
                   :haskell "-XX-"
                   :elm "XXX-"
                   :racket "----"
                   :clojure "XXXX"}}
   {:year 2017
    :language-use {:python "-"
                   :java "*"
                   :c "-"
                   :javascript "*"
                   :C# "-"
                   :haskell "-"
                   :elm "-"
                   :racket "-"
                   :clojure "X"}}])

(def keyword->name
  {:python "Python"
   :java "Java"
   :c "C"
   :javascript "JavaScript"
   :C# "C#"
   :haskell "Haskell"
   :elm "Elm"
   :racket "Racket"
   :clojure "Clojure"})

(def row-order
  [:python :java :c :javascript :C# :haskell :elm :racket :clojure])

(def rows
  (let [create-row (fn [lang-key]
                     (mapv (fn [{:keys [language-use] :as year-data}]
                             [:td {:key (random-uuid)} (language-use lang-key)])
                           programming-history))]
    (mapv (fn [lang-key]
            [:tr {:key lang-key} [:td (keyword->name lang-key)] (create-row lang-key)])
          row-order)))

(def prog-hist-table
  [:table.prog-hist
   [:thead
    [:tr
     [:th "Language"]
     (mapv (fn [{:keys [year]}]
             [:th {:key year} (str year)])
           programming-history)]]
   [:tbody
    rows]])

(def slides
  [(Slide :title [:div.title
                  [:h1 "web-whiteboard"]
                  ;[:h2 "a look at how my first full clojure project works"]
                  [:h2 "a review of my first full clojure project"]
                  [:div.subheader
                   [:h3 "Tom Kidd"]
                   [:h3 "Boston Clojure Meetup"]
                   [:h3 "April/May 2017"]]

                  [:div.repo-links
                   [:h4
                    {:href "https://github.com/tomjkidd/web-whiteboard"}
                    "https://github.com/tomjkidd/web-whiteboard"]
                   [:h4
                    {:href "https://github.com/tomjkidd/web-whiteboard-presentation"}
                    "https://github.com/tomjkidd/web-whiteboard-presentation"]]])
   (Slide :intro [:div.header
                  [:h1 "Introduction"]
                  [:h2 "Who is giving this talk?"]
                  [:ul
                   [:li [:div.bullet "@tomjkidd"]
                    [:ul
                     [:li.subpoint "github, twitter, gmail"]
                     [:li.subpoint "Used racket for a few months in 2015 (Jun-Oct)"]
                     [:li.subpoint "Discovered clojure in 2015 (Dec)"]
                     [:li.subpoint
                      "Timegraph of programming career"
                      [:div.pad-top "X : In use"]
                      [:div "- : Not in use"]
                      [:div.pad-bottom "* : Irregular use"]
                      prog-hist-table]]]
                   [:li [:div.bullet "reifyhealth"]
                    [:ul
                     [:li.subpoint "cljs/clojure"]
                     [:li.subpoint "quiescent, sablono, liberator, postgresql"]]]]])
   (Slide :purpose [:div.purpose
                    [:h1 "Purpose"]
                    [:h2 "What should I expect to learn?"]
                    [:ul
                     [:li.point "Voyage and return"
                      [:ul
                       ;[:li.subpoint--small "not showing anything groundbreaking, just sharing what I learned"]
                       ;[:li.subpoint--small "fertile ground for project growth"]
                       ]]
                     [:li.point "Came from a personal need"
                      [:ul
                       ;[:li.subpoint--small "I want to communicate remotely through drawing"]
                       ]]
                     [:li.point "Well defined, non-trivial"
                      [:ul
                       ;[:li.subpoint--small "prior js experience"]
                       ;[:li.subpoint--small "leverage known information to learn more"]
                       ]]
                     [:li.point "Evaluate the claims of other people"
                      [:ul
                       [:li.subpoint--small "natural distrust of grand claims and groups"]
                       [:li.subpoint--small "better appreciation for design decisions after exploring the space"]]]]])
   (Slide :assumed-knowledge
          [:div
           [:h1 "Assumed Knowledge"]
           [:h2 "What should I know already?"]
           [:ul
            [:li.point "Basic HTML and DOM events"]
            [:li.point "Client/Server architecture"]
            [:li.point "Clojure syntax"]
            [:li.point "Basic drawing skills"]]])
   (Slide :agenda [:div.summary
                    [:h1 "Agenda"]
                    [:h2 "How is the time going to be spent?"]
                    [:ul
                     [:li.point [:div.bullet "Demo"]]
                     [:li.point [:div.bullet "Design"]
                      [:ul
                       [:li.subpoint "building blocks"]
                       [:li.subpoint "client"]
                       [:li.subpoint "server"]]]
                     [:li.point [:div.bullet "Implementation"]
                      [:ul
                       [:li.subpoint "client code dive"]
                       [:li.subpoint "server code dive"]]]
                     [:li.point "Retrospective"]
                     [:li.point "Further Reading"]
                     [:li.point "Questions"]]])
   ;; TODO: Why it's awesome slide(Slide :)
   (Slide :building-blocks
          [:div
           [:h1 "Building Blocks"]
           [:h2 "What are the components of the design?"]
           [:ul
            [:li.point "strokes"
             [:ul
              [:li.subpoint "pendown, penmove, penup"]]]
            [:li.point "events"
             [:ul
              [:li.subpoint "mousedown, mousemove, mouseup"]]]
            [:li.point "functions"
             [:ul
              [:li.subpoint "name, inputs, outputs"]]]
            [:li.point
             "svg elements"]
            [:ul
             [:li.subpoint "circle"]
             [:li.subpoint "path"]] ;; TODO: Create path slide to discuss how it works
            [:li "client"]
            [:li "server"]
            [:li "channels"]]
                                        ;[:img {:src "/function-diagram.svg" :alt "Diagram of what a function can be thought of as"}]
           ])
   (Slide :system-design
          [:div
           [:h1 "System Design"]
           [:h2 "What problems are we trying to solve?"]
           [:ul
            [:li.bullet "Drawing locally (lonely)"]
            [:li.bullet "Drawing with friends (yay!)"]]])
   (Slide :system-implementation
          [:div
           [:h1 "System Implementation"]
           [:h2 "How is the problem solved?"]
           [:ul
            [:li.bullet "web-whiteboard.client"
             [:ul
              [:li.subpoint "core"]
              [:li.subpoint "state"]
              [:li.subpoint "handlers.websocket"]
              [:li.subpoint "ui"]]]
            [:li.bullet "web-whiteboard.server"
             [:ul
              [:li.subpoint "core"]
              [:li.subpoint "handlers.websocket"]]]]])
   (Slide :retrospective
          [:div
           [:h1 "Retrospective"]
           [:h2 "Did the solution solve the problem?"]
           [:ul
            [:li.bullet "Open to ngrok with /index.html?wid=a"]
            [:ul
             [:li.subpoint "~/Downloads/ngrok http 5000"]]]])
   (Slide :retrospective
          [:div
           [:h1 "Retrospective"]
           [:h2 "What claims did you evaluate?"]
           [:ul
            [:li.bullet "Model with data"]
            [:li.bullet "State management"]
            [:li.bullet "Protocols for drawing modes"]
            [:li.bullet "Interop barriers compared to Elm"]
            ]])
   (Slide :retrospective
          [:div
           [:h1 "Retrospective"]
           [:h2 "Are there any parts of the design you are pround of?"]
           [:ul
            [:li.bullet "Handling multiple drawing modes"]
            [:li.bullet "Implemented simple and useful data-structure based dom creation lib, carafe"]
            [:li.bullet "Smoothing algorithm incorporation"]
            [:li.bullet "Keybinding handlers"]]])
   (Slide :retrospective
          [:div
           [:h1 "Retrospective"]

           [:h2 "Are there any parts of the design you are unhappy with/need improvement?"]
           [:ul
            [:li.bullet "Last ui-action in wins"]
            [:li.bullet "draw-handler does mutation in dom directly"]]])
   (Slide :open-questions
          [:div
           [:h1 "Open questions"]
           [:h2 "Can the solution be used to solve another problem?"]
           [:ul
            [:li.bullet "Tango shared log visualization"]]
           [:h2 "What new problems can we go after with the solution?"]
           [:ul
            [:li.bullet "With Tango, could stream events to users that join the whiteboard"]
            [:li.bullet "Circle-mode mosaic-ize photos"]]])
   (Slide :further-reading
          [:div
           [:h1 "Further Reading"]
           [:h2 "What did you use to figure out your solution?"]
           [:ul
            [:li.bullet "RTM"
             [:ul
              [:li.subpoint
               [:a
                {:href "https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent"}
                "MDN Mouse event documentation"]]
              [:li.subpoint
               [:a
                {:href "https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Paths"}
                "MDN SVG Paths Tutorial"]]]]]
           [:ul
            [:li.bullet "Clojure"]
            [:ul
             [:li.subpoint
              [:a
               {:href "https://www.amazon.com/Living-Clojure-Introduction-Training-Developers/dp/1491909048/ref=sr_1_1?ie=UTF8&qid=1492973300&sr=8-1&keywords=living+clojure"}
               "Carin Meier: Living Clojure (2015)"]]
             [:li.subpoint
             [:a
              {:href "http://www.braveclojure.com/foreword/"}
              "Daniel Higgenbotham: Clojure for the Brave and True (2015)"]]]]
           [:ul
            [:li.bullet "Related/Interesting"
             [:ul
              [:li.subpoint
               [:a
                {:href "https://jackschaedler.github.io/handwriting-recognition/"}
                "Jack Schaedler: Back to the Future of Handwriting Recognition (2016)"]]
              [:li.subpoint
               [:a
                {:href "http://www.cs.cornell.edu/~taozou/sosp13/tangososp.pdf"}
                "M. Balakrishnan et al: Tango: Distributed Data Structures over a Shared Log (2013)"]]]]]
           [:ul
            [:li.bullet "Lisp"]
            [:ul
             [:li.subpoint
              [:a
               {:href "https://people.eecs.berkeley.edu/~bh/ss-toc2.html"}
               "B. Harvey M. Wright: Simply Scheme (1999)"]]
             [:li.subpoint
              [:a
               {:href "https://mitpress.mit.edu/sicp/full-text/book/book.html"}
               "H. Abelson G. J. Sussman: Structure and Interpretation of Computer Programs (1996)"]]]]])
 
   (Slide :questions? [:div
                       [:h1 "Questions?"]
                       [:h3 "If you are too shy to ask, feel free to send me an email at "
                        [:a
                         {:href "mailto:tomjkidd@gmail.com"}
                         "tomjkidd@gmail.com"]]])
   (Slide :thanks [:div
                   [:h1 "Thank you"]
                   [:h3 "First talk at Boston Clojure, feedback appreciated"]])])

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
