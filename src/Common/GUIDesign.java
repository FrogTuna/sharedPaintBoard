package Common;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;

import org.json.simple.JSONObject;

import Manager.ConnectionManager;
import Manager.Server;
import Manager.Tools;

public class GUIDesign{
	
	
	//fields
    public static Common.UIlistener listener;
    private String ip = "localhost";
    private int port = 8000;
    private Socket clientSocket;
    private String username;
    public Canvas canvas;
    private static JList<String> myList = new JList<>();

	

    //line button layout
    public JButton line() {

        JButton lineButton = new JButton("Line");
        lineButton.setBounds(20, 50, 100, 30);
        lineButton.setActionCommand("Line");
        lineButton.addActionListener(listener);
        return lineButton;
    }

    //rectangle button layout
    public JButton rectangle() {
        JButton rectangleButton = new JButton("Rectangle");
        rectangleButton.setBounds(20, 80, 100, 30);
        rectangleButton.setActionCommand("Rectangle");
        rectangleButton.addActionListener(listener);
        return rectangleButton;
    }

    
    //triangle button layout
    public JButton triangle() {
        JButton triangleButton = new JButton("Triangle");
        triangleButton.setBounds(20, 110, 100, 30);
        triangleButton.setActionCommand("Triangle");
        triangleButton.addActionListener(listener);
        return triangleButton;
    }

    
    //circle button layout
    public JButton circle() {
        JButton circleButton = new JButton("Circle");
        circleButton.setBounds(20, 140, 100, 30);
        circleButton.setActionCommand("Circle");
        circleButton.addActionListener(listener);
        return circleButton;
    }

    
    //pen button layout
    public JButton pen() {
        JButton penButton = new JButton("Pen");
        penButton.setBounds(20, 170, 100, 30);
        penButton.setActionCommand("Pen");
        penButton.addActionListener(listener);
        return penButton;
    }


    //color button layout
    public JButton colorSelection() {

        JButton colorButton = new JButton("Color");
        colorButton.setBounds(20, 250, 100, 30);
        colorButton.setActionCommand("Color");
        colorButton.addActionListener(listener);
        return colorButton;
    }


   


    //adding text button layout 
    public JButton addText() {

        JButton addTextButton = new JButton("Text");
        addTextButton.setBounds(20, 330, 100, 30);
        addTextButton.setActionCommand("Text");
        addTextButton.addActionListener(listener);
        return addTextButton;
    }

    
    //new button layout
    public JButton newCanvas() {
        JButton newButton = new JButton("New");
        newButton.setBounds(140, 10, 100, 30);
        newButton.addActionListener(new ActionListener() {

        	
        	//listener is to remove all data from list then boardcast to others and reqaint
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.repaint();
                Tools.newCanvas();
                ConnectionManager.clearPaint();
            }

        });
        return newButton;
    }

    
    //save button layout
    public JButton saveCanvas() {
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(250, 10, 100, 30);
        saveButton.addActionListener(new ActionListener() {

        	
            @Override
            public void actionPerformed(ActionEvent e) {
                Tools.saveCanvas();
            }
        });
        return saveButton;
    }

    //opens button layout
    public JButton openCanvas() {
        JButton openButton = new JButton("Open");
        openButton.setBounds(360, 10, 100, 30);
        openButton.addActionListener(new ActionListener() {

        	
        	//popup a new frame allows user to select previously 
        	//save file, only .txt file is allowed
        	//after read all the information, boardcast to others
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JFileChooser fileChooser = new JFileChooser();

                int option = fileChooser.showOpenDialog(Manager.ManagerFrame.frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().contains(".txt")) {
                        
                        return;
                    }
                    Tools.openCanvas(file);
                    for (String line : Tools.allData) {
                        JSONObject object = Tools.parse(line);
                        listener.redraw(object);
                    }
                    ConnectionManager.updatePaintingBoardcast(Tools.allData);
                }

            }
        });
        return openButton;
    }
    
    
    
    //exit button layout
    public JButton exitButton() {
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(470, 10, 100, 30);
        exitButton.addActionListener(new ActionListener() {

        	//boardcast all users ("exit/");
        	//remove all users at the same time
            @Override
            public void actionPerformed(ActionEvent e) {
            	        	
                ConnectionManager.systemOverBoardcast();
                Tools.systemOver();	
            }      
            
        });   
        
        return exitButton;
    }
    
    
    
    
    //display all users in jlist
    public JList<String> displayUser() {
        ListModel<String> listModel = new DefaultComboBoxModel<>(new String[0]);
        myList.setModel(listModel);
        myList.setBounds(700, 50, 85, 300);
        myList.addMouseListener(new MouseAdapter() {
        	
        	//only if the user clicks twice, then remove that user
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedValue = myList.getSelectedValue();
                    ConnectionManager.connectionMap.get(selectedValue).draw("Exit/");
                    ConnectionManager.connectionMap.remove(selectedValue);
                }
            }
        });

        return myList;
    }
    
    
    
    
    //refresh button layout
    public JButton refreshButton() {

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBounds(700, 370, 80, 30);
        refreshButton.setActionCommand("Color");
        refreshButton.addActionListener(new ActionListener() {

        	
        	//update displayuser list when the manager click the button
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	reloadUser();
            	
            }
                  
        });
        
        return refreshButton;
    }

    
    //if the manager delete one user,
    //then update displayuser list
    public static void reloadUser() {
        String[] arr = new String[ConnectionManager.connectionMap.size()];
        int i = 0;
        for (String key : ConnectionManager.connectionMap.keySet()) {
            arr[i++] = key;
        }

        ListModel<String> listModel = new DefaultComboBoxModel<>(arr);
        myList.setModel(listModel);
    }
    


}
