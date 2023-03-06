package tech.inno.controlpanel

import org.apache.kafka.clients.admin.AdminClientConfig
import java.util.*
//import org.apache.kafka.clients.admin.AdminClientConfig

class KafkaClientService {
    val properties=Properties()
        .put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"bootstrap-server-url")


}