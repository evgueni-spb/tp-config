package tech.inno.controlpanel

import io.kubernetes.client.custom.V1Patch
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ConfigMap
import io.kubernetes.client.openapi.models.V1PodList
import io.kubernetes.client.util.Config
import io.kubernetes.client.util.PatchUtils
import io.kubernetes.client.util.PatchUtils.PatchCallFunc
import okhttp3.Call
import org.springframework.stereotype.Service


@Service
class ConfigService {
    private val client = Config.defaultClient()

    fun getData(key:String): String {
        val api = CoreV1Api(client)
       // val items = api.listNode(null, null, null, null, null, null, null, null, 10, false)
        //    .items
        val podList: V1PodList =api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null)

        var result : StringBuilder = StringBuilder()
           result.append("Node list:")
        for (item in podList.items){
            result.append(item.metadata ?.name).append(";")
        }

        return result.toString()
    }

    fun updateUserName(username: String): String {

        val patch="[{\"op\":\"replace\",\"path\":\"/data/username\",\"value\":\"$username\"}]"

        val api = CoreV1Api(client)

        val patchLambda  : () -> Call = {  ->
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
            /* apiClient = */ client)


        return "success"
    }

    /*

    V1Deployment deploy2 =
          PatchUtils.patch(
              V1Deployment.class,
              () ->
                  api.patchNamespacedDeploymentCall(
                      "hello-node",
                      "default",
                      new V1Patch(jsonPatchStr),
                      null,
                      null,
                      null,
                      null, // field-manager is optional
                      null,
                      null),
              V1Patch.PATCH_FORMAT_JSON_PATCH,
              api.getApiClient());
     */


}