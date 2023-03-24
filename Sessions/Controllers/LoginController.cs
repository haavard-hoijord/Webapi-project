using Dapr.Client;
using Microsoft.AspNetCore.Mvc;


    [ApiController]
    public class SessionsController : Controller {
        [HttpPost("/login")]
        public async Task<Dictionary<String, Object>> login([FromBody] Dictionary<String, Object> payload){
            var message = Program.client.CreateInvokeMethodRequest(HttpMethod.Post, "java-api", "session/login", payload);
            return await Program.client.InvokeMethodAsync<Dictionary<String,Object>>(message);
        }

        [HttpPost("/verify")]
        public async Task<bool> verify([FromBody] Dictionary<String, Object> payload){
            var message = Program.client.CreateInvokeMethodRequest(HttpMethod.Post, "java-api", "session/login/verify", payload);
            bool verified = false;
            try{
                verified = await Program.client.InvokeMethodAsync<bool>(message);                
            }catch (Exception){}

            return verified;
        }
    }