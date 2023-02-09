using Dapr;
using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Http;
using System.Text.Json;
using Serilog;
using Serilog.Events;
using Serilog.Sinks.SystemConsole.Themes;

internal class EventProgram
{
    private static void Main(string[] args)
    {        
        Log.Logger = new LoggerConfiguration()
            .WriteTo.Console(theme: AnsiConsoleTheme.Code)
            .WriteTo.Debug()
            .MinimumLevel.Override("Microsoft", LogEventLevel.Warning)
            .WriteTo.Seq("http://seq:5341")
            .CreateLogger();

        var builder = WebApplication.CreateBuilder(args);
        builder.Host.UseSerilog();
        builder.Services.AddControllers().AddDapr();

        var app = builder.Build();
        app.UseSerilogRequestLogging();

        app.Urls.Add("http://*:3001");

        app.MapPost("/event", [Topic("redis-pubsub", "changed")] (HttpRequest request) =>
        {
            var reader = new StreamReader(request.Body);
            var body = reader.ReadToEnd();

            IDictionary<string, object>? json = JsonSerializer.Deserialize<IDictionary<string, object>>(body);

            if(json != null) Log.Information($"Event received, value changed: \"{json["key"]}\", new value: \"{json["value"]}\"");
            return TypedResults.Ok();
        });

        app.UseCloudEvents();
        app.MapControllers();
        app.MapSubscribeHandler();

        app.Run();

        Log.Information("Started event listener!");
    }
}