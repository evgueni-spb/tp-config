package tech.inno.controlpanel

import org.springframework.web.bind.annotation.*

@RestController
class ConfigController (private val configService: ConfigService) {
    @GetMapping("/config")
    @ResponseBody
    fun applyConfig(@RequestParam(name = "param") param : String): String{
      //  val data=configService.getData(param)
        val data=configService.updateUserName(param)
        return "Received $data"
    }

    @GetMapping("/change-service")
    @ResponseBody
    fun changeService(@RequestParam(name = "port") port : String): String{
        val data=configService.modifyServicePort(port.toInt())
        return "Done"
    }

    @GetMapping("/change-deployment")
    @ResponseBody
    fun changeDeployment(@RequestParam(name = "replicas") replicas : String): String{
        val data=configService.modifyPodReplicas(replicas.toInt())
        return "Done"
    }

    @GetMapping("/change-ingress")
    @ResponseBody
    fun changeIngress(@RequestParam(name = "param") param : String): String{

        if (param.equals("add"))
            configService.addRule()
        else
            return configService.changeIngressPort(param)

        return "Done"
    }

    @GetMapping("/opensearch")
    @ResponseBody
    fun modifyOpensearch(@RequestParam(name = "param") param : String): String {
        val data=configService.modifyOpenSearchDeployment(param)
        return "Done"
    }

    @PostMapping("/kafka")
    @ResponseBody
    fun changeKafka(@RequestBody kafkaConfig: KafkaConfig): String {
        if(kafkaConfig.param.isNotEmpty()){
            configService.changeKafkaConfig(kafkaConfig.param)
        }else{
            configService.createKafkaTopic(kafkaConfig.topic)
        }
        return "Done"
    }


    @GetMapping("/change-json-file")
    @ResponseBody
    fun changeJsonFile(): String{
        val data=configService.changeJsonFileInConfigMap("")
        return "Done"
    }

    @GetMapping("/change-yaml-file")
    @ResponseBody
    fun changeYamlFile(): String{
        val data=configService.changeYamlFile()
        return "Done"
    }

    @PostMapping("/change-secret")
    @ResponseBody
    fun changeSecret(): String{
        val data=configService.changeSecret()
        return "Done"
    }

    @PostMapping("/replace-pod")
    @ResponseBody
    fun replacePod(@RequestBody body:String): String{
        val data=configService.replacePod()
        return data
    }


    @PostMapping("/labels")
    @ResponseBody
    fun modifyDeploymentLabels(@RequestBody body:String): String{
        val data=configService.modifyDeploymentLabels()
        return data
    }
}

class KafkaConfig (val param: String, val topic: String){}
