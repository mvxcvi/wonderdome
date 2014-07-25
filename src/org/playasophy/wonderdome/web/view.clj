(ns org.playasophy.wonderdome.web.view)


;;;;; TEMPLATE FUNCTIONS ;;;;;

(defn- button
  ([name]
   (button name name))
  ([name text & {:keys [cls] :or {cls "btn btn-default"}}]
   [:button {:type "button" :class cls :name name} text]))


(defn- head
  "Generates an html <head> section."
  [title & extra]
  [:head
   [:title title]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:link {:href "/css/bootstrap.min.css" :rel "stylesheet" :media "screen"}]
   [:script {:src "/js/jquery.min.js"}]
   extra])


(def ^:const ^:private navbar
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "/"} "Wonderdome"]]
    [:div.collapse.navbar-collapse
     [:ul.nav.navbar-nav
      [:li [:a {:href "/about"} "About"]]
      [:li [:a {:href "mailto:wonderdome@playasophy.org?subject=Feedback"} "Send us feedback!"]]]]]])


(defn- page
  "Generates an html page based on the standard template."
  [head-content body-content]
  [:html
   head-content
   [:body {:style "padding-top: 50px;"}
    navbar
    [:div.container
     body-content]]])



;;;;; PAGE VIEWS ;;;;;

(def about
  (page
    (head "Wonderdome - About")
    [:div.about {:style "padding: 40px 15px;"}
     [:h1 "About the Wonderdome"]
     [:p
      "The Wonderdome is Playasophy Camp's first major venture into LED-based
      eye candy. It consists of 6 strips of 240 LEDs for a total of 1440, each
      of which can be individually controlled in full 24-bit color."]

     [:h2 "Hardware"]
     [:p
      "The Wonderdome is enclosed in a weather-resistant plastic shell with
      ports mounted on the side to hook up power and networking. The system
      includes two power supplies - one that accepts standard 120VAC generator
      power and another that takes 12VDC from a deep-cycle battery. Both power
      supplies convert to the 5VDC that the rest of the system uses."]
     [:p
      "The LED display is built around the "
      [:a {:href "http://www.heroicrobotics.com/products/pixelpusher"} "HeroicRobotics PixelPusher"]
      " and "
      [:a {:href "http://www.illumn.com/other-products/pixelpusher-and-led-strips.html"} "LPD8806 RGB LED strips"]
      ". The PixelPusher presents itself as a network device and listens for
      UDP broadcasts to register with a controller. The controller sends UDP
      packets to the PixelPusher to give pixel color-setting commands."]
     [:p
      "The computer 'brain' running the system is an "
      [:a {:href "http://hardkernel.com/main/products/prdt_info.php"} "ODROID-U3"]
      ", which is a quad-core Snapdragon - the same hardware found in many modern smartphones."]

     [:h2 "Software"]
     [:p
      "The software driving the display is Clojure code running on the JVM. The overall
      system is composed of many individual components communicating via `core.async`
      channels."]]))


(def admin
  (page
    (head "Wonderdome - Admin"
      [:script
       "$(document).ready(function() {
          $('button').click(function() {
            $.post('/admin', {action: $(this).attr('name')});
          });
        });"])
    [:div.admin {:style "padding: 40px 15px; text-align: center;"}
     [:h1 "Wonderdome Administration"]
     (map button ["pause" "resume" "terminate"])]))


(def control
  (page
    (head "Wonderdome - Controls"
      [:script
       "$(document).ready(function(){
          $('button').mousedown(function(){
            $.post('/control',
            {
              button:$(this).attr('name'),
              type:'pressed'
            });
          });
          $('button').mouseup(function(){
            $.post('/control',
            {
              button:$(this).attr('name'),
              type:'released'
            });
          });
        });"])
    [:div.admin {:style "padding: 40px 15px; text-align: center;"}
     [:h1 "Wonderdome Controls"]
     [:p (map button ["up" "down" "left" "right"])]
     [:p (map button ["select" "start"])]
     [:p (map button ["A" "B" "X" "Y"])]
     [:p (map button ["L" "R"])]]))