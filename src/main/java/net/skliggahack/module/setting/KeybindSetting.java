package net.skliggahack.module.setting;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.skliggahack.gui.component.ButtonComponent;
import net.skliggahack.gui.component.Component;
import net.skliggahack.gui.window.Window;
import net.skliggahack.keybind.Keybind;
import net.skliggahack.module.Module;

import static net.skliggahack.SkliggaHack.MC;

public class KeybindSetting extends Setting<Keybind>
{

	private Keybind value;

	private KeybindSetting(Builder builder)
	{
		super(builder.name, builder.description, builder.module);
		value = builder.value;
	}

	@Override
	public Keybind get()
	{
		return value;
	}

	@Override
	public void set(Keybind value)
	{
		this.value = value;
	}

	@Override
	public Component makeComponent(Window parent)
	{
		return new ButtonComponent(parent, 0, 0, 20, getName(), () ->
				MC.setScreen(new Screen(Text.literal(""))
				{

					private final Screen prev = MC.currentScreen;

					@Override
					public boolean keyPressed(KeyInput input)
					{
						value.setKey(input.key());
						MC.setScreen(prev);
						return false;
					}

					@Override
					public void render(DrawContext context, int mouseX, int mouseY, float delta)
					{
						context.drawCenteredTextWithShadow(MC.textRenderer, "Please input your key...", width / 2, height / 2, 0xFFFFFFFF);
					}
				}), () -> String.valueOf(value.getKey()));
	}

	public static class Builder
	{
		private String name;
		private String description;
		private Module module;
		private Keybind value;

		public static Builder newInstance()
		{
			return new Builder();
		}

		public KeybindSetting build()
		{
			return new KeybindSetting(this);
		}

		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}

		public Builder setDescription(String description)
		{
			this.description = description;
			return this;
		}

		public Builder setModule(Module module)
		{
			this.module = module;
			return this;
		}

		public Builder setValue(Keybind value)
		{
			this.value = value;
			return this;
		}
	}
}
