package de.codemakers.bot.supreme.core;

import de.codemakers.bot.supreme.commands.impl.HelpCommand;
import de.codemakers.bot.supreme.commands.impl.fun.MusicCommand;
import de.codemakers.bot.supreme.commands.impl.PingCommand;
import de.codemakers.bot.supreme.commands.impl.fun.TicTacToeCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ChangeCommandPrefixCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ClearCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.CommandCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.GetCommandPrefixCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.GetFileCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ReloadCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.RestartCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.SayCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.SettingsCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.StopCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.UploadFileCommand;
import de.codemakers.bot.supreme.exceptions.ExitTrappedException;
import de.codemakers.bot.supreme.listeners.CommandListener;
import de.codemakers.bot.supreme.listeners.MemberListener;
import de.codemakers.bot.supreme.listeners.ReadyListener;
import de.codemakers.bot.supreme.listeners.VoiceListener;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.SystemOutputStream;
import java.security.Permission;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

/**
 * Supreme-Bot
 *
 * @author Panzer1119
 */
public class SupremeBot {

    public static JDABuilder builder = null;
    public static JDA jda = null;
    private static boolean running = false;

    public static final void main(String[] args) {
        try {
            System.setOut(new SystemOutputStream(System.out, false));
            System.setErr(new SystemOutputStream(System.err, true));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Standard.STANDARD_SETTINGS.saveSettings();
                Standard.saveAllGuildSettings();
            }));
            reload();
            builder = new JDABuilder(AccountType.BOT);
            builder.setAutoReconnect(true);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setGame(new Game() {
                @Override
                public String getName() {
                    return "V" + Standard.VERSION;
                }

                @Override
                public String getUrl() {
                    return null;
                }

                @Override
                public GameType getType() {
                    return null;
                }
            });
            initListeners();
            initCommands();
            initPlugins();
            startJDA();
        } catch (Exception ex) {
            System.err.println("Main Error: " + ex);
            ex.printStackTrace();
        }
    }

    private static final boolean initListeners() {
        try {
            builder.addEventListener(new ReadyListener());
            builder.addEventListener(new VoiceListener());
            builder.addEventListener(new MemberListener());
            builder.addEventListener(new CommandListener());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final boolean initCommands() {
        try {
            //Normal Commands
            new HelpCommand();
            new PingCommand();
            //Fun Commands
            new MusicCommand();
            new TicTacToeCommand();
            //Moderation Commands
            new ChangeCommandPrefixCommand();
            new ClearCommand();
            new CommandCommand();
            new GetCommandPrefixCommand();
            new GetFileCommand();
            new ReloadCommand();
            new RestartCommand();
            new SayCommand();
            new SettingsCommand();
            new StopCommand();
            new UploadFileCommand();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final boolean initPlugins() {
        try {
            Standard.loadPlugins();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean isRunning() {
        return running;
    }

    public static final boolean startJDA() {
        if (running) {
            return true;
        }
        running = true;
        try {
            builder.setToken(new String(Standard.getToken()));
            jda = builder.buildBlocking();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static final boolean stopJDA(boolean now) {
        if (!running) {
            return true;
        }
        try {
            if (now) {
                jda.shutdownNow();
            } else {
                jda.shutdown();
            }
            running = false;
            reload();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static final boolean restartJDA(boolean now) {
        stopJDA(now);
        return startJDA();
    }

    public static final boolean reload() {
        try {
            reloadSettings();
            reloadPermissionRoles();
            loadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean reloadSettings() {
        try {
            Standard.reloadSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean reloadPermissionRoles() {
        try {
            Standard.reloadPermissionRoles();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean loadAllGuilds() {
        try {
            Standard.loadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean reloadGuilds() {
        try {
            Standard.reloadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean reloadGuildSettings() {
        try {
            Standard.reloadAllGuildSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean reloadGuildSettings(String guild_id) {
        try {
            Standard.getGuildSettings(guild_id).loadSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean stopCompletely(int status) {
        Standard.runShutdownHooks();
        stopJDA(true);
        exit(status);
        return true;
    }
    
    private static final boolean exit(int status) {
        superSecurityManager.exit(status);
        return true;
    }
    
    private static final SuperSecurityManager superSecurityManager = new SuperSecurityManager();
    
    private static final class SuperSecurityManager extends SecurityManager {
        
        private static final String SETSECURITYMANAGER = "setSecurityManager";
        
        private boolean enabled = true;
        
        public SuperSecurityManager() {
            System.setSecurityManager(this);
        }

        @Override
        public final void checkExit(int status) {
            if (enabled) {
                throw new ExitTrappedException("!!!WARNING SOMEONE WANTED TO EXIT THE SYSTEM!!!");
            }
        }

        @Override
        public final void checkPermission(Permission permission) {
            if (permission == null || permission.getName() == null) {
                return;
            }
            if (permission.getName().equals(SETSECURITYMANAGER) || permission.getName().equalsIgnoreCase(SETSECURITYMANAGER) || permission.getName().startsWith(SETSECURITYMANAGER)) {
                throw new SecurityException("!!!WARNING SOMEONE WANTED TO CHANGE THE SECURITYMANAGER!!!");
            }
        }
        
        public final SuperSecurityManager exit(int status) {
            enabled = false;
            System.exit(status);
            enabled = true;
            return this;
        }

        @Override
        public final String toString() {
            return "SecurityManager by Panzer1119";
        }
        
    }

}
