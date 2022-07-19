package ge.gabramishvilimavaliani.messagingapp.model

data class Messages(var from: String? = null,
                    var message: String? = null,
                    var to: String? = null,
                    var messageID: String? = null,
                    var time: String? = null,
                    var date: String? = null)