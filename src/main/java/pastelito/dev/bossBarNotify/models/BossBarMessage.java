package pastelito.dev.bossBarNotify.models;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class BossBarMessage {
    
    private final String message;
    private final BarColor color;
    private final BarStyle style;
    private final int time;
    
    public BossBarMessage(String message, BarColor color, BarStyle style, int time) {
        this.message = message;
        this.color = color;
        this.style = style;
        this.time = time;
    }
    
    public String getMessage() {
        return message;
    }
    
    public BarColor getColor() {
        return color;
    }
    
    public BarStyle getStyle() {
        return style;
    }
    
    public int getTime() {
        return time;
    }
}
