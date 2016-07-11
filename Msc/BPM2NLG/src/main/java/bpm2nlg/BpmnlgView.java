package bpm2nlg;
import general.language.common.realizer.RealizedText;

import java.awt.Cursor;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;


public class BpmnlgView {

	// GUI components declaration
	private JFrame frame;
	private JTextField tfProcessModel;
	private JButton btnTransform;
	private JButton btnCopy;
	private JButton btnExportTxt ;
	private JTextArea taProcessText;
	private JScrollPane spProcessText;
	// end components declarations
	
	private BpmnlgMain bpmnlg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BpmnlgView window = new BpmnlgView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BpmnlgView() {
		initialize();
		bpmnlg = new BpmnlgMain();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		frame.setBounds(100, 100, 900, 423);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 874, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mnFile.add(mntmSaveAs);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 19, 864, 59);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Input"));
		frame.getContentPane().add(panel);
		
		JLabel lblProcessModelFile = new JLabel("Process model file path (.json)");
		panel.add(lblProcessModelFile);
		
		tfProcessModel = new JTextField();
		tfProcessModel.setToolTipText("Enter the path for the process model to be converted to text");
		tfProcessModel.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				tfProcessModelFocusLost(arg0);
			}
		});
		tfProcessModel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(tfProcessModel);
		tfProcessModel.setColumns(40);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setToolTipText("search the file in your system");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearchActionPerformed(e);
			}
		});
		btnSearch.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnSearch);
		
		btnTransform = new JButton("Transform to Text");
		btnTransform.setToolTipText("convert the model to text");
		btnTransform.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTransformActionPerformed(e);
			}
		});
		btnTransform.setEnabled(false);
		btnTransform.setBounds(382, 78, 172, 23);
		frame.getContentPane().add(btnTransform);
		btnTransform.setVerticalAlignment(SwingConstants.BOTTOM);
		
		taProcessText = new JTextArea();
		taProcessText.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				taProcessTextUpdated(arg0);
			}
		});
		taProcessText.setBounds(10, 102, 864, 239);
		taProcessText.setEditable(false);
		spProcessText = new JScrollPane(taProcessText);
		spProcessText.setBounds(10, 102, 864, 239);
		spProcessText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		frame.getContentPane().add(spProcessText);
		
		btnExportTxt = new JButton("Export to TXT");
		btnExportTxt.setEnabled(false);
		btnExportTxt.setBounds(249, 352, 127, 23);
		frame.getContentPane().add(btnExportTxt);
		
		btnCopy = new JButton("Copy to clipboard");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCopyClipboardActionPerformed(e);
			}
		});
		btnCopy.setEnabled(false);
		btnCopy.setBounds(582, 352, 140, 23);
		frame.getContentPane().add(btnCopy);
	}
	
	private void tfProcessModelFocusLost(CaretEvent arg0){
		boolean btnTransformEnabled = !tfProcessModel.getText().trim().isEmpty();
		btnTransform.setEnabled(btnTransformEnabled);
	}
	
	private void taProcessTextUpdated(CaretEvent arg0){
		boolean btnExportEnabled = !taProcessText.getText().trim().isEmpty();
		btnCopy.setEnabled(btnExportEnabled);
		btnExportTxt.setEnabled(btnExportEnabled);
	}
	
	private void btnTransformActionPerformed(ActionEvent e){
		try {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			String modelPath = tfProcessModel.getText();
			RealizedText modelText = bpmnlg.convertToModelToText(modelPath);
			taProcessText.setText(modelText.getFormattedText());
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(frame, ex.getMessage(), "BPM2NLG", JOptionPane.ERROR_MESSAGE);
		} finally {
			frame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	private void btnSearchActionPerformed(ActionEvent e){
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			tfProcessModel.setText(file.getAbsolutePath());
		}
	}
	
	private void btnCopyClipboardActionPerformed(ActionEvent e){
		StringSelection stringSelection = new StringSelection (taProcessText.getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		clpbrd.setContents (stringSelection, null);
		
		JOptionPane.showMessageDialog(frame, "Success: The text was copied to your clipboard!", "BPM2NLG", JOptionPane.INFORMATION_MESSAGE);
	}
}
