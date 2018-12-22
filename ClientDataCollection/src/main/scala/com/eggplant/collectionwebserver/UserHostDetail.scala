package com.eggplant.collectionwebserver

final case class UserHostDetail(
   customerID: String,
   hostname: String,
   path: String,
   ipAddress: String,
   eventStartTime: String,
   eventFinishtime: String,
   userAgent: String
  )
