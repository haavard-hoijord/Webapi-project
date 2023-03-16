using Dapr.Client;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using System.Text;
using Serilog;
using Serilog.Events;
using Serilog.Sinks.SystemConsole.Themes;

internal class Program
{
    private static void Main(string[] args)
    {
        using var client = new DaprClientBuilder().Build();

        string DAPR_STORE_NAME = "sqlserver.statestore";
        string DAPR_PUBSUB_NAME = "redis-pubsub";

        Log.Logger = new LoggerConfiguration()
            .WriteTo.Console(theme: AnsiConsoleTheme.Code)
            .WriteTo.Debug()
            .MinimumLevel.Override("Microsoft", LogEventLevel.Warning)
            .WriteTo.Seq("http://seq:5341")
            .CreateLogger();

        var builder = WebApplication.CreateBuilder(args);
        builder.Host.UseSerilog();

        var app = builder.Build();

        app.UseSerilogRequestLogging();

        app.Urls.Add("http://*:3000");
        app.Urls.Add("http://*:5000");

        app.MapGet("/value/{key}", async (string key) =>
        {
            return await client.GetStateAsync<string>(DAPR_STORE_NAME, key) ?? "Not found";
        });

        app.MapPost("/value/{key}", async (HttpRequest request) =>
        {
            StreamReader reader = new StreamReader(request.Body, Encoding.UTF8);
            string? requestKey = request.RouteValues["key"]?.ToString();
            string requestValue = await reader.ReadToEndAsync();

            Log.Information($"Updating \"{requestKey}\" to {requestValue}");

            await client.SaveStateAsync(DAPR_STORE_NAME, requestKey, requestValue);
            await client.PublishEventAsync(DAPR_PUBSUB_NAME, "changed", new { key = requestKey, value = requestValue });
        });

        app.Run();

        Log.Information("Started server!");
    }
}