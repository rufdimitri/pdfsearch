package com.ztz.gridbagconstraintsbuilder;

import static java.awt.GridBagConstraints.*;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBagContraintsBuilder {

	private final GridBagConstraints initialConfiguration;

	private int    xPos;
	private int    yPos;
	private int    width;
	private int    height;
	private double weightX;
	private double weightY;
	private int    anchor;
	private int    fill;
	private Insets insets;
	private int    paddingX;
	private int    paddingY;

	public GridBagContraintsBuilder() {
		this(new GridBagConstraints(0, 0, 1, 1, 0, 0, WEST, NONE, new Insets(0, 0, 0, 0), 0, 0));
	}

	public GridBagContraintsBuilder(GridBagConstraints initialConfiguration) {
		this.initialConfiguration = initialConfiguration;
		resetToInitialConfiguration();
	}

	private void resetToInitialConfiguration() {
		xPos = initialConfiguration.gridx;
		yPos = initialConfiguration.gridy;
		width = initialConfiguration.gridwidth;
		height = initialConfiguration.gridheight;
		weightX = initialConfiguration.weightx;
		weightY = initialConfiguration.weighty;
		anchor = initialConfiguration.anchor;
		fill = initialConfiguration.fill;
		insets = initialConfiguration.insets;
		paddingX = initialConfiguration.ipadx;
		paddingY = initialConfiguration.ipady;
	}

	public GridBagContraintsBuilder x(int xPos) {
		this.xPos = xPos;
		return this;
	}

	public GridBagContraintsBuilder y(int yPos) {
		this.yPos = yPos;
		return this;
	}

	public GridBagContraintsBuilder width(int width) {
		this.width = width;
		return this;
	}

	public GridBagContraintsBuilder height(int height) {
		this.height = height;
		return this;
	}

	public GridBagContraintsBuilder weightX(double weightX) {
		this.weightX = weightX;
		return this;
	}

	public GridBagContraintsBuilder weightY(double weightY) {
		this.weightY = weightY;
		return this;
	}

	public GridBagContraintsBuilder anchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GridBagContraintsBuilder fill(int fill) {
		this.fill = fill;
		return this;
	}

	public GridBagContraintsBuilder insets(Insets insets) {
		return insets(insets.top, insets.left, insets.bottom, insets.right);
	}

	public GridBagContraintsBuilder insets(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}

	/**
	 * Set the same inset at top, left, bottom and right
	 */
	public GridBagContraintsBuilder insets(int inset) {
		this.insets = new Insets(inset, inset, inset, inset);
		return this;
	}

	public GridBagContraintsBuilder fillNone() {
		this.fill = NONE;
		this.weightX = 0;
		this.weightY = 0;
		return this;
	}

	public GridBagContraintsBuilder fillHorizontal(double weightX) {
		this.fill = HORIZONTAL;
		this.weightX = weightX;
		return this;
	}

	public GridBagContraintsBuilder fillVertical(double weightY) {
		this.fill = VERTICAL;
		this.weightY = weightY;
		return this;
	}

	public GridBagContraintsBuilder fillBoth(double weightX, double weightY) {
		this.fill = BOTH;
		this.weightX = weightX;
		this.weightY = weightY;
		return this;
	}

	public GridBagContraintsBuilder rowRemainder() {
		this.width = REMAINDER;
		return this;
	}

	public GridBagContraintsBuilder notRemainder() {
		this.width = 1;
		return this;
	}

	public GridBagContraintsBuilder colRemainder() {
		this.height = REMAINDER;
		return this;
	}

	public GridBagContraintsBuilder west() {
		this.anchor = WEST;
		return this;
	}

	public GridBagContraintsBuilder east() {
		this.anchor = EAST;
		return this;
	}

	public GridBagContraintsBuilder north() {
		this.anchor = NORTH;
		return this;
	}

	public GridBagContraintsBuilder northEast() {
		this.anchor = NORTHEAST;
		return this;
	}

	public GridBagContraintsBuilder northWest() {
		this.anchor = NORTHWEST;
		return this;
	}

	public GridBagContraintsBuilder center() {
		this.anchor = CENTER;
		return this;
	}

	public GridBagContraintsBuilder south() {
		this.anchor = SOUTH;
		return this;
	}

	public GridBagContraintsBuilder southEast() {
		this.anchor = SOUTHEAST;
		return this;
	}

	public GridBagContraintsBuilder southWest() {
		this.anchor = SOUTHWEST;
		return this;
	}

	public GridBagContraintsBuilder lineStart() {
		this.anchor = LINE_START;
		return this;
	}

	public GridBagContraintsBuilder lineEnd() {
		this.anchor = LINE_END;
		return this;
	}

	public GridBagContraintsBuilder firstLineStart() {
		this.anchor = FIRST_LINE_START;
		return this;
	}

	public GridBagContraintsBuilder firstLineEnd() {
		this.anchor = FIRST_LINE_END;
		return this;
	}

	public GridBagContraintsBuilder lastLineStart() {
		this.anchor = LAST_LINE_START;
		return this;
	}

	public GridBagContraintsBuilder lastLineEnd() {
		this.anchor = LAST_LINE_END;
		return this;
	}

	public GridBagContraintsBuilder pageStart() {
		this.anchor = PAGE_START;
		return this;
	}

	public GridBagContraintsBuilder pageEnd() {
		this.anchor = PAGE_END;
		return this;
	}

	/**
	 * Jump to the next row and consider the current row height Set the x position to 0
	 */
	public GridBagContraintsBuilder newRow() {
		this.yPos += height;
		this.xPos = 0;
		return this;
	}

	/**
	 * Jump to the next col and consider the current column width
	 */
	public GridBagContraintsBuilder newCol() {
		this.xPos += width;
		return this;
	}

	/**
	 * Build the GridBagContraints and keep the current configuration
	 *
	 * @return GridBagConstraints with the given configuration
	 */
	public GridBagConstraints build() {
		return new GridBagConstraints(xPos, yPos, width, height, weightX, weightY, anchor, fill, insets, paddingX, paddingY);
	}

	/**
	 * Build the GridBagContraints and reset to the initial configuration
	 *
	 * @return GridBagConstraints with the given configuration
	 */
	public GridBagConstraints buildAndReset() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints(xPos, yPos, width, height, weightX, weightY, anchor, fill, insets, paddingX, paddingY);
		resetToInitialConfiguration();
		return gridBagConstraints;
	}
}
