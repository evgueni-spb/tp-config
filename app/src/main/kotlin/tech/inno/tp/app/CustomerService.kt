package tech.inno.tp.app

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class CustomerService {
    @Value("\${config.userpath}")
    private val pathToUserFile: String? = null

    /**
     * return user name from config map
     */
    fun getUsername(): String {
        //check if the file exists, then get the username from the file
        if (File(pathToUserFile).exists()){
            val username  = File(pathToUserFile).readText(Charsets.UTF_8)
            return username.trimEnd('\n', '\r')
        }else{
            return ""
        }
    }

    fun getDescription(): String {
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()
        val jsonString: String = this.javaClass.getResource("/customer-config.json").readText(Charsets.UTF_8)
        val customerConfig : CustomerConfig? = mapper.readValue<CustomerConfig>(jsonString)
        customerConfig ?.let {
            return customerConfig.position.plus(":").plus(customerConfig.status)
        }
        return  "Sample description"

        /*

        val jsonString: String = File("./src/main/resources/films.json").readText(Charsets.UTF_8)
    val jsonTextList:List<Film> = mapper.readValue<List<Film>>(jsonString)
    for (film in jsonTextList) {
        println(film)
    }
         */


     //   return "Sample description"
    }

}