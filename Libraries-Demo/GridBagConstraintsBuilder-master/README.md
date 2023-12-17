# GridBagConstraintsBuilder
A Builder for Swing GridBagConstraints to ease the creation of Gridbag-Layouts. 
Instead of having a long parameter list of the GridBagConstraints constructor the builder
provides named methods.

Construct the builder with default values
```Java
	xPos = 0;
	yPos = 0;
	width = 1;
	height = 1;
	weightX = 0;
	weightY = 0;
	anchor = GridBagConstraints.WEST;
	fill = GridBagConstraints.NONE;
	insets = new Insets(0, 0, 0, 0);
	paddingX = 0;
	paddingY = 0;
```

or use constructor with ```Java GridBagContraintsBuilder(GridBagConstraints initialConfiguration) ``` to set own default values for reset.

There are two approches to place the compontents on the grid:

1.) Explicit setting x and y pos:

```Java
	GridBagContraintsBuilder gbb = new GridBagContraintsBuilder();
	mainPanel.add(firstRowFirstCol, gbb.x(0).y(0).insets(5).buildAndReset());
	mainPanel.add(firstRowSecondCol, gbb.x(1).y(0).insets(5).rowRemainder().buildAndReset());
	mainPanel.add(secondRowFirstCol, gbb.x(0).y(1).insets(5).buildAndReset());
```

2.) Working column by column and row by row
```Java
	GridBagContraintsBuilder gbb = new GridBagContraintsBuilder();
	mainPanel.add(firstRowFirstCol, gbb.x(0).y(0).insets(5).build());
	mainPanel.add(firstRowSecondCol, gbb.newCol().insets(5).rowRemainder().build());
	mainPanel.add(secondRowFirstCol, gbb.newRow().insets(5).build());
```


Example:

```Java
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
```

![Screenshot](gridbagcontraintsbuilder-test/resources/exampleScreenshot.JPG?raw=true "Title")



