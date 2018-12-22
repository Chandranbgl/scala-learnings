object HelloWorld {
  def main(args: Array[String]): Unit = {
    println("HelloWorld")
  }
}



kafka_offset = ev.metadata.`kafk     kafka_offset: String = "",
kafka_partition = ev.metadata.`      kafka_partition: String = "",
service = ev.metadata.service,       service: String = "",
event_name = ev.metadata.eventN      event_name: String = "",
event_id = ev.metadata.eventId,      event_id: String = "",
session_id = ev.metadata.sessio      session_id: String = "",
submitted_at =  UdfUtilities.co      occurred_at: String = "",
occurred_at =  UdfUtilities.con      submitted_at: String = "",
restaurant_id = ev.payload.rest      restaurant_id : String = "",
restaurant_type = ev.payload.re      restaurant_type : String = "",
restaurant_name  = ev.payload.r      restaurant_name : String = "",
max_diners  = ev.payload.maxDin      max_diners : Int = 0,
cuisines = "",                       cuisines : String= "",
chain = ev.payload.chain.name,       chain: String = "",
brand = ev.payload.brand.name,       brand: String = "",
primary_image = ev.payload.prim      primary_image: String = "",
website = ev.payload.website,        website: String = "",
booking_telephone = ev.payload.      booking_telephone: String = "",
booking_email = ev.payload.book      booking_email: String = "",
booking_url = ev.payload.bookin      booking_url: String = "",
is_booking_required = ev.payloa      is_booking_required: Boolean = false,
country = ev.payload.address.co      country: String = "",
postcode = ev.payload.address.p      postcode: String = "",
building = ev.payload.address.b      building :String= "",
street = ev.payload.address.str      street  : String = "",
town = ev.payload.address.town,      town  : String = "",
county = ev.payload.address.cou      county  : String = "",
geo_lat = ev.payload.geo.lat,        geo_lat  : String = "",
geo_long = ev.payload.geo.long,      geo_long  : String = "",
description = ev.payload.descri      description : String = "",
deleted_at  = ev.payload.delete      deleted_at : String = "",
updated_at = ev.payload.updated      updated_at : String = "",
other_images = "", ev.payload        other_images: String = "",
row_create_date = date,              row_create_date  : String = "",
row_created_by = "EventSink" ,       row_update_date  : String = "",
row_update_date = date ,             row_created_by  : String = "",
row_updated_by = "EventSink",        row_updated_by  : String = "",
source = "EventSink",                source  : String = "",
deleted = "N"                        deleted : String = ""