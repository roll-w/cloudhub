package org.huel.cloudhub.file.server.command;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * @author RollW
 */
@Component
public class CommandPromptProvider implements PromptProvider {
    @Override
    public AttributedString getPrompt() {
        return new AttributedString("cloudhub> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    }
}
