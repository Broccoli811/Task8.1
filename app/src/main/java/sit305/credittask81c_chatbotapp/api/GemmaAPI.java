package sit305.credittask81c_chatbotapp.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import sit305.credittask81c_chatbotapp.models.GemmaRequest;
import sit305.credittask81c_chatbotapp.models.GemmaResponse;
public interface GemmaAPI {
    @Headers({"Content-Type: application/json"})
    @POST("chat")
    Call<GemmaResponse> sendMessage(@Body GemmaRequest request);
}
