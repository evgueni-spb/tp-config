package tech.inno.tp.app

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(private val customerService: CustomerService){
    @GetMapping("/customer")
    fun getCustomer(): Customer = Customer(
        firstName = "Evgueni",
        lastName = "Chakine",
        age = 52,
        description = customerService.getDescription(),
        userName = customerService.getUsername()
    )
}