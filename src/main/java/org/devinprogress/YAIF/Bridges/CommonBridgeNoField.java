package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreen;
import org.devinprogress.YAIF.InputFieldWrapper;
import org.devinprogress.YAIF.YetAnotherInputFix;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;

/**
 * Created by recursiveg on 14-11-12.
 */
public class CommonBridgeNoField extends BaseActionBridge{
    private final GuiScreen gui;
    private final InputFieldWrapper wrapper;
    private Method keyTypedMethod=null;

    public CommonBridgeNoField(GuiScreen screen,InputFieldWrapper wrapper){
        YetAnotherInputFix.log("CommonBridgeNoField Initialized. %s", this);
        gui=screen;
        this.wrapper=wrapper;
        try{
            keyTypedMethod=gui.getClass().getDeclaredMethod(YetAnotherInputFix.ObfuscatedEnv?"func_73869_a":"keyTyped",char.class,int.class);
            keyTypedMethod.setAccessible(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean needShow(){
        if(keyTypedMethod==null){
            YetAnotherInputFix.log("failed to determine keyTypedMethod @%s",gui);
            return false;
        }
        return true;
    }

    @Override
    public void bindKeys(JTextField tf){
        super.bindKeys(tf);

        bindKey(tf, KeyEvent.VK_ENTER,"enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String str=wrapper.getText();
                if(str.equals("")){
                    dispatch(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                keyTypedMethod.invoke(gui,'\n',28);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                wrapper.setText("");
                dispatch(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            for(int i=0;i<str.length();++i)
                                keyTypedMethod.invoke(gui,str.charAt(i),-1);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        bindKey(tf, KeyEvent.VK_ESCAPE, "esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                YetAnotherInputFix.log("CommonBridgeNoField ESC Pressed");
                wrapper.bridgeQuit();
            }
        });

        bindKey(tf, KeyEvent.VK_BACK_SPACE, "backsp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t=wrapper.getText();
                if(t.equals("")){
                    dispatch(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                keyTypedMethod.invoke(gui,' ',14);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                int pos=wrapper.getCaretPosition();
                t=t.substring(0,pos-1)+t.substring(pos);
                wrapper.setText(t,pos-1);
            }
        });
    }
}
