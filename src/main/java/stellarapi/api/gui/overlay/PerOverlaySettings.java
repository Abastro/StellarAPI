package stellarapi.api.gui.overlay;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.gui.pos.EnumHorizontalPos;
import stellarapi.api.gui.pos.EnumVerticalPos;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyString;

public class PerOverlaySettings extends SimpleConfigHandler {

	private EnumHorizontalPos horizontal;
	private EnumVerticalPos vertical;
	
	private ConfigPropertyString propHorizontal;
	private ConfigPropertyString propVertical;
	
	void initializeSetttings(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
		this.setHorizontal(horizontal);
		this.setVertical(vertical);
		
		this.propHorizontal = new ConfigPropertyString("Horizontal_Position", "", horizontal.name());
		this.propVertical = new ConfigPropertyString("Vertical_Position", "", vertical.name());
		
		this.addConfigProperty(this.propHorizontal);
		this.addConfigProperty(this.propVertical);
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		super.setupConfig(config, category);
		
		propHorizontal.setValidValues(EnumHorizontalPos.names);
		propHorizontal.setComment("Horizontal Position on the Overlay.");
		propHorizontal.setRequiresMcRestart(false);
		propHorizontal.setLanguageKey("config.property.gui.pos.horizontal");
		
		propVertical.setValidValues(EnumVerticalPos.names);
		propVertical.setComment("Vertical Position on the Overlay.");
		propVertical.setRequiresMcRestart(false);
		propVertical.setLanguageKey("config.property.gui.pos.vertical");
	}
	
	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.setHorizontal(EnumHorizontalPos.valueOf(propHorizontal.getString()));
		this.setVertical(EnumVerticalPos.valueOf(propVertical.getString()));
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propHorizontal.setString(getHorizontal().name());
		propVertical.setString(getVertical().name());
		super.saveToConfig(config, category);
	}

	public EnumHorizontalPos getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(EnumHorizontalPos horizontal) {
		this.horizontal = horizontal;
	}

	public EnumVerticalPos getVertical() {
		return vertical;
	}

	public void setVertical(EnumVerticalPos vertical) {
		this.vertical = vertical;
	}

}
