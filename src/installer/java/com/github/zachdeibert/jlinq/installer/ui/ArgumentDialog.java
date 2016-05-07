package com.github.zachdeibert.jlinq.installer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.github.zachdeibert.jlinq.installer.Arguments;
import com.github.zachdeibert.jlinq.installer.ConversionType;

public class ArgumentDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final JTextField outputDirectory;
	private final JTextField javaDirectory;
	private final JTextField jarFile;
	private final JComboBox<ConversionType> conversionType;
	private final Arguments args;
	private ArgumentSavedListener argumentSavedListener;

	public void addArgumentSavedListener(final ArgumentSavedListener l) {
		if ( l == null ) {
			return;
		}
		if ( argumentSavedListener == null ) {
			argumentSavedListener = l;
		} else {
			final ArgumentSavedListener old = argumentSavedListener;
			argumentSavedListener = e -> {
				old.argumentSaved(e);
				l.argumentSaved(e);
			};
		}
	}

	protected void dispatchArgumentSavedEvent(final ArgumentSavedEvent e) {
		if ( argumentSavedListener != null ) {
			argumentSavedListener.argumentSaved(e);
		}
	}

	@Override
	public void dispose() {
		args.outputDirectory = new File(outputDirectory.getText());
		args.javaDirectory = new File(javaDirectory.getText());
		args.jar = new File(jarFile.getText());
		args.type = (ConversionType) conversionType.getSelectedItem();
		dispatchArgumentSavedEvent(new ArgumentSavedEvent(this, ArgumentSavedEvent.ARGUMENT_SAVED_EVENT, args));
		setVisible(false);
		super.dispose();
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		if ( !b ) {
			System.exit(0);
		}
	}

	private void browse(final JTextField field) {
		final JFileChooser dialog = new JFileChooser(new File("."));
		dialog.setSelectedFile(new File(field.getText()));
		dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if ( dialog.showDialog(this, "Accept") == JFileChooser.APPROVE_OPTION ) {
			field.setText(dialog.getSelectedFile().getAbsolutePath());
		}
	}

	public ArgumentDialog() {
		args = new Arguments();
		setTitle("JLINQ Installer");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 99, 0, 0, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 15, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblJlinqInstaller = new JLabel("JLINQ Installer");
			lblJlinqInstaller.setAlignmentY(Component.TOP_ALIGNMENT);
			GridBagConstraints gbc_lblJlinqInstaller = new GridBagConstraints();
			gbc_lblJlinqInstaller.gridwidth = 4;
			gbc_lblJlinqInstaller.insets = new Insets(0, 0, 5, 5);
			gbc_lblJlinqInstaller.gridx = 0;
			gbc_lblJlinqInstaller.gridy = 0;
			contentPanel.add(lblJlinqInstaller, gbc_lblJlinqInstaller);
		}
		{
			JSeparator separator = new JSeparator();
			GridBagConstraints gbc_separator = new GridBagConstraints();
			gbc_separator.gridwidth = 3;
			gbc_separator.insets = new Insets(0, 0, 5, 5);
			gbc_separator.gridx = 0;
			gbc_separator.gridy = 1;
			contentPanel.add(separator, gbc_separator);
		}
		{
			JLabel lblOutputDirectory = new JLabel("Output Directory:");
			GridBagConstraints gbc_lblOutputDirectory = new GridBagConstraints();
			gbc_lblOutputDirectory.anchor = GridBagConstraints.EAST;
			gbc_lblOutputDirectory.gridwidth = 2;
			gbc_lblOutputDirectory.insets = new Insets(0, 0, 5, 5);
			gbc_lblOutputDirectory.gridx = 0;
			gbc_lblOutputDirectory.gridy = 2;
			contentPanel.add(lblOutputDirectory, gbc_lblOutputDirectory);
		}
		{
			outputDirectory = new JTextField();
			GridBagConstraints gbc_outputDirectory = new GridBagConstraints();
			gbc_outputDirectory.insets = new Insets(0, 0, 5, 5);
			gbc_outputDirectory.fill = GridBagConstraints.HORIZONTAL;
			gbc_outputDirectory.gridx = 2;
			gbc_outputDirectory.gridy = 2;
			contentPanel.add(outputDirectory, gbc_outputDirectory);
			outputDirectory.setColumns(10);
		}
		{
			JButton btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(e -> browse(outputDirectory));
			GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
			gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
			gbc_btnBrowse.gridx = 3;
			gbc_btnBrowse.gridy = 2;
			contentPanel.add(btnBrowse, gbc_btnBrowse);
		}
		{
			JLabel lblJavaDirectory = new JLabel("Java Directory:");
			GridBagConstraints gbc_lblJavaDirectory = new GridBagConstraints();
			gbc_lblJavaDirectory.anchor = GridBagConstraints.EAST;
			gbc_lblJavaDirectory.gridwidth = 2;
			gbc_lblJavaDirectory.insets = new Insets(0, 0, 5, 5);
			gbc_lblJavaDirectory.gridx = 0;
			gbc_lblJavaDirectory.gridy = 3;
			contentPanel.add(lblJavaDirectory, gbc_lblJavaDirectory);
		}
		{
			javaDirectory = new JTextField();
			GridBagConstraints gbc_javaDirectory = new GridBagConstraints();
			gbc_javaDirectory.insets = new Insets(0, 0, 5, 5);
			gbc_javaDirectory.fill = GridBagConstraints.HORIZONTAL;
			gbc_javaDirectory.gridx = 2;
			gbc_javaDirectory.gridy = 3;
			contentPanel.add(javaDirectory, gbc_javaDirectory);
			javaDirectory.setColumns(10);
		}
		{
			JButton btnBrowse_1 = new JButton("Browse");
			btnBrowse_1.addActionListener(e -> browse(javaDirectory));
			GridBagConstraints gbc_btnBrowse_1 = new GridBagConstraints();
			gbc_btnBrowse_1.insets = new Insets(0, 0, 5, 0);
			gbc_btnBrowse_1.gridx = 3;
			gbc_btnBrowse_1.gridy = 3;
			contentPanel.add(btnBrowse_1, gbc_btnBrowse_1);
		}
		{
			JLabel lblJar = new JLabel("Jar:");
			GridBagConstraints gbc_lblJar = new GridBagConstraints();
			gbc_lblJar.anchor = GridBagConstraints.EAST;
			gbc_lblJar.gridwidth = 2;
			gbc_lblJar.insets = new Insets(0, 0, 5, 5);
			gbc_lblJar.gridx = 0;
			gbc_lblJar.gridy = 4;
			contentPanel.add(lblJar, gbc_lblJar);
		}
		{
			jarFile = new JTextField();
			GridBagConstraints gbc_jarFile = new GridBagConstraints();
			gbc_jarFile.insets = new Insets(0, 0, 5, 5);
			gbc_jarFile.fill = GridBagConstraints.HORIZONTAL;
			gbc_jarFile.gridx = 2;
			gbc_jarFile.gridy = 4;
			contentPanel.add(jarFile, gbc_jarFile);
			jarFile.setColumns(10);
		}
		{
			JButton btnBrowse_2 = new JButton("Browse");
			btnBrowse_2.addActionListener(e -> browse(jarFile));
			GridBagConstraints gbc_btnBrowse_2 = new GridBagConstraints();
			gbc_btnBrowse_2.insets = new Insets(0, 0, 5, 0);
			gbc_btnBrowse_2.gridx = 3;
			gbc_btnBrowse_2.gridy = 4;
			contentPanel.add(btnBrowse_2, gbc_btnBrowse_2);
		}
		{
			JLabel lblConversionType = new JLabel("Conversion Type:");
			GridBagConstraints gbc_lblConversionType = new GridBagConstraints();
			gbc_lblConversionType.anchor = GridBagConstraints.EAST;
			gbc_lblConversionType.gridwidth = 2;
			gbc_lblConversionType.insets = new Insets(0, 0, 5, 5);
			gbc_lblConversionType.gridx = 0;
			gbc_lblConversionType.gridy = 5;
			contentPanel.add(lblConversionType, gbc_lblConversionType);
		}
		{
			conversionType = new JComboBox<ConversionType>();
			conversionType.setModel(new DefaultComboBoxModel<ConversionType>(ConversionType.values()));
			GridBagConstraints gbc_conversionType = new GridBagConstraints();
			gbc_conversionType.gridwidth = 2;
			gbc_conversionType.insets = new Insets(0, 0, 5, 5);
			gbc_conversionType.fill = GridBagConstraints.HORIZONTAL;
			gbc_conversionType.gridx = 2;
			gbc_conversionType.gridy = 5;
			contentPanel.add(conversionType, gbc_conversionType);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Install");
				okButton.addActionListener(e -> dispose());
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(e -> System.exit(0));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		pack();
	}
}
