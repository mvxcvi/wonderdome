(ns org.playasophy.wonderdome.mode.config
  "Collected mode configurations."
  (:require
    (org.playasophy.wonderdome.mode
      ant
      flicker
      lantern
      pulse
      rainbow
      strobe
      dart 
      tunes)
    potemkin))


(potemkin/import-vars
  (org.playasophy.wonderdome.mode.ant ant)
  (org.playasophy.wonderdome.mode.flicker flicker)
  (org.playasophy.wonderdome.mode.lantern lantern)
  (org.playasophy.wonderdome.mode.pulse pulse)
  (org.playasophy.wonderdome.mode.rainbow rainbow)
  (org.playasophy.wonderdome.mode.strobe strobe)
  (org.playasophy.wonderdome.mode.dart dart)
  (org.playasophy.wonderdome.mode.tunes tunes))
