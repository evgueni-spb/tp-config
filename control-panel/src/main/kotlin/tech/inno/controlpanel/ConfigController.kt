package tech.inno.controlpanel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigController (private val configService: ConfigService) {
    @GetMapping("/config")
    @ResponseBody
    fun applyConfig(@RequestParam(name = "param") param : String): String{
      //  val data=configService.getData(param)
        val data=configService.updateUserName(param)
        return "Received $data"
    }
}