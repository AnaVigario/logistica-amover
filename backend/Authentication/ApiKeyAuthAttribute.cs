using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using projeto.Controllers;

namespace projeto.Authentication
{
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
    public class ApiKeyAuthAttribute : Attribute, IAsyncActionFilter
    {
        private const string ApiKeyHeaderName = "x-api-key";

        public async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
        {
            if (!context.HttpContext.Request.Headers.TryGetValue(ApiKeyHeaderName, out var extractedApiKey))
            {
                context.Result = new ContentResult()
                {
                    StatusCode = 401,
                    Content = "{\"status\": \"unauthorized\", \"code\": 401, \"description\": \"A API Key está ausente ou é inválida, impedindo a autenticação da aplicação.\"}",
                    ContentType = "application/json"
                };
                return;
            }

            var dbOperations = context.HttpContext.RequestServices.GetRequiredService<DatabaseOperations>();
            var apiKey = dbOperations.GetApiKey(extractedApiKey);

            if (apiKey == null)
            {
                context.Result = new ContentResult()
                {
                    StatusCode = 401,
                    Content = "{\"status\": \"unauthorized\", \"code\": 401, \"description\": \"A API Key está ausente ou é inválida, impedindo a autenticação da aplicação.\"}",
                    ContentType = "application/json"
                };
                return;
            }

            await next();
        }
    }
}
