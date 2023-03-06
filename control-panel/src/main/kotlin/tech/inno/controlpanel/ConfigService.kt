package tech.inno.controlpanel

import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.apis.AppsV1Api
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.apis.NetworkingV1Api
import io.kubernetes.client.openapi.models.V1ConfigMap
import io.kubernetes.client.openapi.models.V1Deployment
import io.kubernetes.client.openapi.models.V1PodList
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.openapi.models.V1Service
import io.kubernetes.client.util.Config
import io.kubernetes.client.util.PatchUtils
import io.kubernetes.client.util.wait.Wait
import okhttp3.Call
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime


@Service
class ConfigService {
    private val client = Config.defaultClient()

    init {
        client.setDebugging(true)
    }

    fun getData(key: String): String {
        val api = CoreV1Api(client)
        // val items = api.listNode(null, null, null, null, null, null, null, null, 10, false)
        //    .items
        val podList: V1PodList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)

        var result: StringBuilder = StringBuilder()
        result.append("Node list:")
        for (item in podList.items) {
            result.append(item.metadata?.name).append(";")
        }

        return result.toString()
    }

    fun updateUserName(username: String): String {

        val patch = "[{\"op\":\"replace\",\"path\":\"/data/username\",\"value\":\"$username\"}]"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedConfigMapCall(
                "user-config",
                "default",
                V1Patch(patch),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1ConfigMap::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )


        return "success"
    }


    fun modifyServicePort(portNumber: Int): Unit {
        val patch = "[{\"op\":\"replace\",\"path\":\"/spec/ports/0/port\",\"value\":$portNumber}]"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedServiceCall(
                "config-poc-1",
                "default",
                V1Patch(patch),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1Service::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )

    }

    fun modifyPodReplicas(numReplicas: Int): Unit {
        val patch = "[{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\":$numReplicas}]"

        val appsApi = AppsV1Api(client)

        val patchLambda: () -> Call = { ->
            appsApi.patchNamespacedDeploymentCall(
                "config-poc-1",
                "default",
                V1Patch(patch),
                null,
                null,
                "kubectl-rollout",
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1Deployment::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )
    }

    fun addRule() {
        TODO("Not yet implemented")
    }

    fun changeIngressPort(param: String): String {
        //client.basePath = "http://localhost"

        // Configure API key authorization: BearerToken

        // Configure API key authorization: BearerToken
        //      val BearerToken = client.getAuthentication("BearerToken") as ApiKeyAuth
        //      BearerToken.apiKey = "YOUR API KEY"
        // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
        //BearerToken.setApiKeyPrefix("Token");

        // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null)
        //BearerToken.setApiKeyPrefix("Token");
        val apiInstance = NetworkingV1Api(client)
        val name = "config-poc" // String | name of the Ingress

        val namespace = "default" // String | object name and auth scope, such as for teams and projects

        val portNumber = param.toInt()

        val patch =
            "[{\"op\":\"replace\",\"path\":\"/spec/rules/0/http/paths/0/backend/service/port/number\",\"value\":$portNumber}]"

        val body = V1Patch(patch) // V1Patch |


        try {
            val result = apiInstance.patchNamespacedIngress(
                name,
                namespace,
                body,
                null,
                null,
                null,
                null,
                null
            )
            return result.toString()
        } catch (e: ApiException) {
            System.err.println("Exception when calling NetworkingV1Api#patchNamespacedIngress")
            System.err.println("Status code: " + e.code)
            System.err.println("Reason: " + e.responseBody)
            System.err.println("Response headers: " + e.responseHeaders)
            e.printStackTrace()
            return e.responseBody
        }
    }


    fun modifyOpenSearchDeployment(param: String): String {

        val appsApi = AppsV1Api(client)
        // Trigger a rollout restart of the example deployment
        val deploymentName = "opensearch-dashboards-2-1677594122"
        val namespace = "default"
        val runningDeployment = appsApi.readNamespacedDeployment(deploymentName, namespace, null);

        // Explicitly set "restartedAt" annotation with current date/time to trigger rollout when patch
        // is applied
        runningDeployment
            .getSpec()
            ?.getTemplate()
            ?.getMetadata()
            ?.putAnnotationsItem("kubectl.kubernetes.io/restartedAt", LocalDateTime.now().toString())
        runningDeployment
            .getSpec()
            ?.replicas=param.toInt()


        try {
            val deploymentJson = client.getJSON().serialize(runningDeployment);

            PatchUtils.patch(
                V1Deployment::class.java,
                {
                    appsApi.patchNamespacedDeploymentCall(
                        deploymentName,
                        namespace,
                        V1Patch(deploymentJson),
                        null,
                        null,
                        "kubectl-rollout",
                        null,
                        null,
                        null
                    )
                },
                V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
                client
            )

            // Wait until deployment has stabilized after rollout restart
            val processLambda: () -> Boolean = { ->

                println ("Waiting for READY status")
                val readyReplicas = appsApi
                    .readNamespacedDeployment(deploymentName, namespace, null)
                    .status
                    ?.readyReplicas

               readyReplicas!! > 0
            }

            Wait.poll(
                Duration.ofSeconds(3),
                Duration.ofSeconds(60),
                processLambda
            )

            return "Done"

        }catch (e: ApiException){
            return e.responseBody
        }

    }


    fun changeJsonFileInConfigMap(param: String) {

        val updatedFile= "{\\\"position\\\": \\\"Lead architect\\\", \\\"status\\\": \\\"active\\\"}"

        val patch = "[{\"op\":\"replace\",\"path\":\"/data/config.json\",\"value\":\"$updatedFile\"}]"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedConfigMapCall(
                "file-config",
                "default",
                V1Patch(patch),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1ConfigMap::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )
    }

    fun strategicMergeChangeJsonFile(){
        //another_config.json: |
        //    {"name":"Evgueni","age":52}

        val patchString="{\\\"data\\\": {\\\"another_config.json\\\": \\\"{\\\\\\\"name\\\\\\\":\\\\\\\"Evgeny\\\\\\\",\\\\\\\"age\\\\\\\":52}\\\"}}"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedConfigMapCall(
                "file-config",
                "default",
                V1Patch(patchString),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1ConfigMap::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
            /* apiClient = */ client
        )


    }

    fun changeYamlFile(){

        //app-name: billing app
        //    status: active
        //    billing-mode:
        //      payment-plan: monthly
        //      deposit: enabled


        val updatedFile= "app-name: billing app\\n" +
                "status: active\\n" +
                "billing-mode:\\n" +
                "  payment-plan: yearly\\n" +
                "  deposit: disabled"

        val patch = "[{\"op\":\"replace\",\"path\":\"/data/config.yml\",\"value\":\"$updatedFile\"}]"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedConfigMapCall(
                "yaml-file-config",
                "default",
                V1Patch(patch),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1ConfigMap::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )
    }

    fun changeSecret(){
        val updatedFile= "bW9kaWZpZWRzZWNyZXQK"

        val patch = "[{\"op\":\"replace\",\"path\":\"/data/.secret-file\",\"value\":\"$updatedFile\"}]"

        val api = CoreV1Api(client)

        val patchLambda: () -> Call = { ->
            api.patchNamespacedSecretCall(
                "simple-secret",
                "default",
                V1Patch(patch),
                null,
                null,
                null,
                null,
                null,
                null
            )

        }

        PatchUtils.patch(/* apiTypeClass = */ V1Secret::class.java,
            /* callFunc = */ patchLambda,
            /* patchFormat = */ V1Patch.PATCH_FORMAT_JSON_PATCH,
            /* apiClient = */ client
        )
    }

    fun changeKafkaConfig(param: String) {

    }

    fun createKafkaTopic(topic: String) {

    }


}