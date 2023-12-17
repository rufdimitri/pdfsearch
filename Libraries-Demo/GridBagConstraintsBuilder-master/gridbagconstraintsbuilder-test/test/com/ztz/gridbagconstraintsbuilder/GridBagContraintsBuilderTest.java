package com.ztz.gridbagconstraintsbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GridBagContraintsBuilderTest {

	public static void main(String[] args) {
		TestFrame frame = new TestFrame();
		frame.init();
		frame.pack();
		Dimension preferredSize = frame.getPreferredSize();
		frame.setMinimumSize(preferredSize);
		frame.setSize(new Dimension((int) (preferredSize.width * 1.5), (int) (preferredSize.getHeight() * 1.8)));
		frame.setVisible(true);
	}

	private static class TestFrame extends JFrame {

		private final JPanel mainPanel          = new JPanel(new GridBagLayout());
		private final JLabel firstRowFirstCol   = new JLabel("First row - first col");
		private final JLabel firstRowSecondCol  = new JLabel("First row - second col - row remainder");
		private final JLabel secondRowFirstCol  = new JLabel("Second row - first col");
		private final JLabel secondRowSecondCol = new JLabel("Second row - second col");
		private final JLabel secondRowThirdCol  = new JLabel("Second row - third col");
		private final JLabel thirdRowFirstCol   = new JLabel("Third row - first col");
		private final JLabel thirdRowSecondCol  = new JLabel("Third row - second col - row remainder - grow horizontal");
		private final JLabel fourthRowFirstCol  = new JLabel("Fourth row - first col");
		private final JLabel fourthRowSecondCol = new JLabel("Fourth row - second col - south");
		private final JLabel fourthRowThirdCol  = new JLabel("Fourth row - third col - grow both directions");
		private final JLabel fifthRowFirstCol  = new JLabel("Fifth row - first col");
		private final JLabel fifthRowSecondCol = new JLabel("Fifth row - second col");
		private final JLabel fifthRowThirdCol  = new JLabel("Fifth row - third col - east");

		private void init() {
			setTitle("GridBagBuilder Example");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().add(mainPanel);

			GridBagContraintsBuilder gbb = new GridBagContraintsBuilder();

			mainPanel.add(firstRowFirstCol, gbb.x(0).y(0).insets(5).buildAndReset());
			mainPanel.add(firstRowSecondCol, gbb.x(1).y(0).insets(5).rowRemainder().buildAndReset());

			mainPanel.add(secondRowFirstCol, gbb.x(0).y(1).insets(5).buildAndReset());
			mainPanel.add(secondRowSecondCol, gbb.x(1).y(1).insets(5).buildAndReset());
			mainPanel.add(secondRowThirdCol, gbb.x(2).y(1).insets(5).buildAndReset());

			mainPanel.add(thirdRowFirstCol, gbb.x(0).y(2).insets(5).buildAndReset());
			mainPanel.add(thirdRowSecondCol, gbb.x(1).y(2).insets(5).expandHorizontal().rowRemainder().buildAndReset());

			mainPanel.add(fourthRowFirstCol, gbb.x(0).y(3).insets(5).buildAndReset());
			mainPanel.add(fourthRowSecondCol, gbb.x(1).y(3).insets(5).south().buildAndReset());
			mainPanel.add(fourthRowThirdCol, gbb.x(2).y(3).insets(5).expandBoth().buildAndReset());

			mainPanel.add(fifthRowFirstCol, gbb.x(0).y(4).insets(5).buildAndReset());
			mainPanel.add(fifthRowSecondCol, gbb.x(1).y(4).insets(5).buildAndReset());
			mainPanel.add(fifthRowThirdCol, gbb.x(2).y(4).insets(5).east().buildAndReset());

			setBackgroundColors();
		}

		private void setBackgroundColors() {
			List<JLabel> allLabels = getAllLabels();
			for (JLabel label : allLabels) {
				label.setOpaque(true);
				label.setBackground(Color.LIGHT_GRAY);
			}
		}

		private List<JLabel> getAllLabels() {
			return Arrays.stream(mainPanel.getComponents()).filter(component -> component instanceof JLabel).map(label -> (JLabel)label).collect(Collectors.toList());
		}
	}
}
