package net.java.sip.communicator.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.Insets;


//import samples.accessory.StringGridBagLayout;

/**
 * Sample login splash screen
 */
public class ForwardSplash
    extends JDialog
{

    JTextField forwardTextField = null;

    /**
     * Command string for a cancel action (e.g., a button).
     * This string is never presented to the user and should
     * not be internationalized.
     */
    private String CMD_FORWARD = "cmd.forward";
    private String CMD_CANCEL = "cmd.cancel";

    // Components we need to manipulate after creation
    private JButton forwardButton = null;
    private JButton cancelButton = null;
    
    protected String forwarded;
 

    /**
     * Creates new form ForwardSplash
     */
    public ForwardSplash(Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        pack();
        centerWindow();
    }


    /**
     * Centers the window on the screen.
     */
    private void centerWindow()
    {
        Rectangle screen = new Rectangle(
            Toolkit.getDefaultToolkit().getScreenSize());
        Point center = new Point(
            (int) screen.getCenterX(), (int) screen.getCenterY());
        Point newLocation = new Point(
            center.x - this.getWidth() / 2, center.y - this.getHeight() / 2);
        if (screen.contains(newLocation.x, newLocation.y,
                            this.getWidth(), this.getHeight())) {
            this.setLocation(newLocation);
        }
    } // centerWindow()

    /**
     *
     * We use dynamic layout managers, so that layout is dynamic and will
     * adapt properly to user-customized fonts and localized text. The
     * GridBagLayout makes it easy to line up components of varying
     * sizes along invisible vertical and horizontal grid lines. It
     * is important to sketch the layout of the interface and decide
     * on the grid before writing the layout code.
     *
     * Here we actually use
     * our own subclass of GridBagLayout called StringGridBagLayout,
     * which allows us to use strings to specify constraints, rather
     * than having to create GridBagConstraints objects manually.
     *
     *
     * We use the JLabel.setLabelFor() method to connect
     * labels to what they are labeling. This allows mnemonics to work
     * and assistive to technologies used by persons with disabilities
     * to provide much more useful information to the user.
     */
    private void initComponents()
    {
        Container contents = getContentPane();
        contents.setLayout(new BorderLayout());

        String title = "Forwarding";

        setTitle(title);
        setResizable(false);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent event)
            {
                dialogDone(CMD_CANCEL);
            }
        });

        // Accessibility -- all frames, dialogs, and applets should
        // have a description
        getAccessibleContext().setAccessibleDescription("Forward Splash");

        String forwardPromptLabelValue  = "Enter the target of the forwarding";
        
        JLabel splashLabel = new JLabel(forwardPromptLabelValue );
        splashLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        splashLabel.setHorizontalAlignment(SwingConstants.CENTER);
        splashLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        contents.add(splashLabel, BorderLayout.NORTH);

        JPanel centerPane = new JPanel();
        centerPane.setLayout(new GridBagLayout());

        forwardTextField = new JTextField(); // needed below

        // user name label
        JLabel forwardLabel = new JLabel();
        forwardLabel.setDisplayedMnemonic('t');
        // setLabelFor() allows the mnemonic to work
        forwardLabel.setLabelFor(forwardTextField);


        String forwardFromLabelValue = "Forward to :";
        
      

        forwardLabel.setText(forwardFromLabelValue);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.WEST;
        c.insets=new Insets(12,12,0,0);
        centerPane.add(forwardLabel, c);

        // user name text
        c = new GridBagConstraints();
        c.gridx=1;
        c.gridy=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1.0;
        c.insets=new Insets(12,7,0,11);
        centerPane.add(forwardTextField, c);
        
        
        // Buttons along bottom of window
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, 0));

        forwardButton = new JButton();
        forwardButton.setText("Ok");
        forwardButton.setActionCommand(CMD_FORWARD);
        forwardButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dialogDone(event);
            }
        });
        buttonPanel.add(forwardButton);
        
        // space
        buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setActionCommand(CMD_CANCEL);
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                dialogDone(event);
            }
        });
        buttonPanel.add(cancelButton);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(11, 12, 11, 11);

        centerPane.add(buttonPanel, c);

        contents.add(centerPane, BorderLayout.CENTER);
        getRootPane().setDefaultButton(forwardButton);

    } // initComponents()


    /**
     * The user has selected an option. Here we close and dispose the dialog.
     * If actionCommand is an ActionEvent, getCommandString() is called,
     * otherwise toString() is used to get the action command.
     *
     * @param actionCommand may be null
     */
    private void dialogDone(Object actionCommand)
    {
        String cmd = null;
        if (actionCommand != null) {
            if (actionCommand instanceof ActionEvent) {
                cmd = ( (ActionEvent) actionCommand).getActionCommand();
            }
            else {
                cmd = actionCommand.toString();
            }
        }
        if (cmd == null) {
            // do nothing
        }
        else if (cmd.equals(CMD_FORWARD)) {
        	forwarded = forwardTextField.getText();

        }
        else if (cmd.equals(CMD_CANCEL)) {
        	forwarded = null;
        }

        setVisible(false);
        dispose();
    } // dialogDone()
} // class LoginSplash
