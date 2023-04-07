import com.bueno.impl.dummybot.DummyBot;
import com.caue.isa.destroyerbot.DestroyerBot;

module bot.impl {
    requires bot.spi;
    exports com.bueno.impl.dummybot;
    provides com.bueno.spi.service.BotServiceProvider with DummyBot, DestroyerBot;
}