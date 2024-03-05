package com.instaapp.clothingtemplate1.dataModel

data class ShipmentData(
    val ShipmentData: ArrayList<Shipment>
)

data class Shipment(
    val Shipment: ShipmentDetails
)

data class ShipmentDetails(
    val AWB: String,
    val CODAmount: Int,
    val ChargedWeight: Any?,
    val Consignee: Consignee,
    val DeliveryDate: Any?,
    val DestRecieveDate: Any?,
    val Destination: String,
    val DispatchCount: Int,
    val Ewaybill: ArrayList<Any>,
    val ExpectedDeliveryDate: Any?,
    val Extras: String,
    val FirstAttemptDate: Any?,
    val InvoiceAmount: Int,
    val OrderType: String,
    val Origin: String,
    val OriginRecieveDate: Any?,
    val OutDestinationDate: Any?,
    val PickUpDate: String,
    val PickedupDate: Any?,
    val PickupLocation: String,
    val PromisedDeliveryDate: Any?,
    val Quantity: String,
    val RTOStartedDate: Any?,
    val ReferenceNo: String,
    val ReturnPromisedDeliveryDate: Any?,
    val ReturnedDate: Any?,
    val ReverseInTransit: Boolean,
    val Scans: ArrayList<Scan>,
    val SenderName: String,
    val Status: ShipmentStatus
)

data class Consignee(
    val Address1: ArrayList<Any>,
    val Address2: ArrayList<Any>,
    val Address3: String,
    val City: String,
    val Country: String,
    val Name: String,
    val PinCode: Int,
    val State: String,
    val Telephone1: String,
    val Telephone2: String
)

data class ScanDetail(
    val Instructions: String,
    val Scan: String,
    val ScanDateTime: String,
    val ScanType: String,
    val ScannedLocation: String,
    val StatusCode: String,
    val StatusDateTime: String
)

data class Scan(
    val ScanDetail: ScanDetail
)

data class ShipmentStatus(
    val Instructions: String,
    val RecievedBy: String,
    val Status: String,
    val StatusCode: String,
    val StatusDateTime: String,
    val StatusLocation: String,
    val StatusType: String
)

