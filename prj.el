












































(jde-set-project-name "default")
(jde-set-variables 
 '(jde-gen-to-string-method-template (quote ("'&" "\"public String toString() {\" 'n>" "\"return super.toString();\" 'n>" "\"}\" 'n>")))
 '(jde-bug-jre-home "")
 '(jde-bug-debugger-host-address "ailab02.cacs.usl.edu")
 '(jde-help-docsets (quote (("javadoc" "doc/" nil))))
 '(jde-run-option-heap-profile (quote (nil "./java.hprof" 5 20 "Allocation objects")))
 '(jde-compile-option-encoding nil)
 '(jde-compile-option-optimize nil)
 '(jde-run-executable-args nil)
 '(jde-compile-option-verbose nil)
 '(jde-gen-buffer-templates (quote (("Class" . jde-gen-class) ("Console" . jde-gen-console) ("Swing App" . jde-gen-jfc-app))))
 '(jde-run-option-classpath nil)
 '(jde-gen-mouse-listener-template (quote ("'& (P \"Component name: \")" "\".addMouseListener(new MouseAdapter() {\" 'n>" "\"public void mouseClicked(MouseEvent e) {}\" 'n>" "\"public void mouseEntered(MouseEvent e) {}\" 'n>" "\"public void mouseExited(MouseEvent e) {}\" 'n>" "\"public void mousePressed(MouseEvent e) {}\" 'n>" "\"public void mouseReleased(MouseEvent e) {}});\" 'n>")))
 '(jde-make-args "")
 '(jde-gen-jfc-app-buffer-template (quote ("(funcall jde-gen-boilerplate-function) 'n" "\"import java.awt.Dimension;\" 'n" "\"import java.awt.Graphics;\" 'n" "\"import java.awt.Graphics2D;\" 'n" "\"import java.awt.Color;\" 'n" "\"import java.awt.geom.Ellipse2D;\" 'n" "\"import java.awt.event.WindowAdapter;\" 'n" "\"import java.awt.event.WindowEvent;\" 'n" "\"import javax.swing.JFrame;\" 'n" "\"import javax.swing.JPanel;\" 'n" "\"import javax.swing.JScrollPane;\" 'n" "\"import javax.swing.JMenuBar;\" 'n" "\"import javax.swing.JMenu;\" 'n" "\"import java.awt.event.ActionEvent;\" 'n" "\"import javax.swing.AbstractAction;\" 'n 'n" "\"/**\" 'n" "\" * \"" "(file-name-nondirectory buffer-file-name) 'n" "\" *\" 'n" "\" *\" 'n" "\" * Created: \" (current-time-string) 'n" "\" *\" 'n" "\" * @author \" (user-full-name) 'n" "\" * @version\" 'n" "\" */\" 'n>" "'n>" "\"public class \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" extends JFrame\"" "\" {\"  'n>" "" "\"class Canvas extends JPanel\"" "\" {\"  'n>" "" "\"public Canvas () \"" "\" {\"  'n>" "" "\"setSize(getPreferredSize());\" 'n>" "\"Canvas.this.setBackground(Color.white);\" 'n>" "\"}\" 'n> 'n>" "\"public Dimension getPreferredSize() \"" "\" {\"  'n>" "" "\"return new Dimension(600, 600);\" 'n>" "\"}\" 'n> 'n>" "\"public void paintComponent(Graphics g) \"" "\" {\"  'n>" "" "\"super.paintComponent(g);\" 'n>" "\"Graphics2D g2d = (Graphics2D) g;\" 'n>" "\"Ellipse2D circle = new Ellipse2D.Double(0d, 0d, 100d, 100d);\" 'n>" "\"g2d.setColor(Color.red);\" 'n>" "\"g2d.translate(10, 10);\" 'n>" "\"g2d.draw(circle);\" 'n>" "\"g2d.fill(circle);\" 'n>" "\"}\" 'n> 'n>" "\"}\" 'n> 'n>" "\"public \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\"()\"" "\" {\"  'n>" "" "\"super(\\\"\" (P \"Enter app title: \") \"\\\");\" 'n>" "\"setSize(300, 300);\" 'n>" "\"addWindowListener(new WindowAdapter() \"" "\" {\"  'n>" "" "\"public void windowClosing(WindowEvent e) {System.exit(0);}\" 'n>" "\"public void windowOpened(WindowEvent e) {}\" 'n>" "\"});\" 'n>" "\"setJMenuBar(createMenu());\" 'n>" "\"getContentPane().add(new JScrollPane(new Canvas()));\" 'n>" "\"}\" 'n>" "'n>" "\"public static void main(String[] args) \"" "\" {\"  'n>" "" "'n>" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" f = new \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\"();\" 'n>" "\"f.show();\" 'n>" "'p 'n>" "\"}\" 'n> 'n>" "\"protected JMenuBar createMenu() \"" "\" {\"  'n>" "" "\"JMenuBar mb = new JMenuBar();\" 'n>" "\"JMenu menu = new JMenu(\\\"File\\\");\" 'n>" "\"menu.add(new AbstractAction(\\\"Exit\\\") \"" "\" {\"  'n>" "" "\"public void actionPerformed(ActionEvent e) \"" "\" {\"  'n>" "" "\"System.exit(0);\" 'n>" "\"}\" 'n>" "\"});\" 'n>" "\"mb.add(menu);\" 'n>" "\"return mb;\" 'n>" "\"}\" 'n> 'n>" "\"} // \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "'n>")))
 '(jde-gen-cflow-while (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"while\")" "'(l > \"while (\" (p \"while-clause: \" clause) \") \"" "\"{\" > n> r n" "\"} // end of while (\" (s clause) \")\" > n>)" ")")))
 '(jde-bug-breakpoint-marker-colors (quote ("red" . "yellow")))
 '(jde-db-source-directories (quote ("/home/swamp2/maida/cxg9789/cs555/Neuroidal/")))
 '(jde-db-debugger (quote ("Other" "jdb" . "Executable")))
 '(jde-db-marker-regexp "^Breakpoint hit: .*(\\([^$]*\\).*:\\([0-9]*\\))")
 '(jde-compile-option-deprecation nil)
 '(jde-javadoc-see-tag-template (quote ("* @see " ref)))
 '(jde-javadoc-since-tag-template (quote ("* @since 1.0")))
 '(jde-javadoc-version-tag-template (quote ("* @version 1.0")))
 '(jde-imenu-include-classdef t)
 '(jde-javadoc-describe-method-template (quote ("* Describe " (jde-javadoc-code name) " method here.")))
 '(jde-gen-inner-class-template (quote ("'& \"class \" (P \"Class name: \" class)" "(P \"Superclass: \" super t)" "(let ((parent (jde-gen-lookup-named 'super)))" "(if (not (string= parent \"\"))" "(concat \" extends \" parent))) \" {\" 'n>" "\"public \" (s class) \"() {\" 'n> \"}\" 'n> \"}\" 'n>")))
 '(jde-run-applet-viewer "")
 '(jde-run-read-vm-args nil)
 '(jde-imenu-sort nil)
 '(jde-run-java-vm-w "javaw")
 '(jde-run-option-application-args nil)
 '(jde-quote-classpath t)
 '(jde-enable-abbrev-mode t)
 '(jde-gen-action-listener-template (quote ("'& (P \"Component name: \")" "\".addActionListener(new ActionListener() {\" 'n>" "\"public void actionPerformed(ActionEvent e) {\" 'n>" "\"}});\" 'n>")))
 '(jde-gen-class-buffer-template (quote ("(funcall jde-gen-boilerplate-function) 'n" "\"// $\" \"Id$\" 'n" "\"/**\" 'n" "\" * \"" "(file-name-nondirectory buffer-file-name) 'n" "\" *\" 'n" "\" *\" 'n" "\" * <p>Created: \" (current-time-string) 'n" "\" * <p>Modified: $\" \"Date$\" 'n" "\" *\" 'n" "\" * @author \" (user-full-name) 'n" "\" * @version $\" \"Revision$ for this file.\" 'n" "\" */\" 'n>" "'n>" "\"public class \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" \" (jde-gen-get-super-class)" "\" {\"  'n>" "" "\"public \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" ()\"" "\" {\"  'n>" "" "'p 'n>" "\"}\">" "'n>" "'n>" "\"}\">" "\"// \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "'n>")))
 '(jde-gen-boilerplate-function (quote jde-gen-create-buffer-boilerplate))
 '(jde-compile-option-classpath (quote ("../javarun")))
 '(jde-gen-code-templates (quote (("Get Set Pair" . jde-gen-get-set) ("toString method" . jde-gen-to-string-method) ("Action Listener" . jde-gen-action-listener) ("Window Listener" . jde-gen-window-listener) ("Mouse Listener" . jde-gen-mouse-listener) ("Mouse Motion Listener" . jde-gen-mouse-motion-listener) ("Inner Class" . jde-gen-inner-class) ("println" . jde-gen-println) ("property change support" . jde-gen-property-change-support) ("EJB Entity Bean" . jde-gen-entity-bean) ("EJB Session Bean" . jde-gen-session-bean))))
 '(jde-mode-abbreviations (quote (("ab" . "abstract") ("bo" . "boolean") ("br" . "break") ("by" . "byte") ("byv" . "byvalue") ("cas" . "cast") ("ca" . "catch") ("ch" . "char") ("cl" . "class") ("co" . "const") ("con" . "continue") ("de" . "default") ("dou" . "double") ("el" . "else") ("ex" . "extends") ("fa" . "false") ("fi" . "final") ("fin" . "finally") ("fl" . "float") ("fo" . "for") ("fu" . "future") ("ge" . "generic") ("go" . "goto") ("impl" . "implements") ("impo" . "import") ("ins" . "instanceof") ("in" . "int") ("inte" . "interface") ("lo" . "long") ("na" . "native") ("ne" . "new") ("nu" . "null") ("pa" . "package") ("pri" . "private") ("pro" . "protected") ("pu" . "public") ("re" . "return") ("sh" . "short") ("st" . "static") ("su" . "super") ("sw" . "switch") ("sy" . "synchronized") ("th" . "this") ("thr" . "throw") ("throw" . "throws") ("tra" . "transient") ("tr" . "true") ("vo" . "void") ("vol" . "volatile") ("wh" . "while"))))
 '(jde-compile-option-command-line-args "")
 '(jde-imenu-include-signature t)
 '(jde-compile-option-verbose-path nil)
 '(jde-db-startup-commands nil)
 '(jde-build-use-make nil)
 '(jde-appletviewer-option-vm-args nil)
 '(jde-run-executable "")
 '(jde-gen-buffer-boilerplate nil)
 '(jde-compile-option-depend-switch (quote ("-Xdepend")))
 '(jde-compile-option-sourcepath (quote ("/home/swamp2/maida/cxg9789/cs555/Neuroidal")))
 '(jde-run-mode-hook nil)
 '(jde-gen-println (quote ("'&" "\"System.out.println(\" (P \"Print out: \") \");\" 'n>")))
 '(jde-javadoc-exception-tag-template (quote ("* @exception " type " if an error occurs")))
 '(jde-gen-get-set-var-template (quote ("'n>" "(P \"Variable type: \" type) \" \"" "(P \"Variable name: \" name) \";\" 'n> 'n>" "\"/**\" 'n>" "\"* Get the value of \" (s name) \".\" 'n>" "\"* @return value of \" (s name) \".\" 'n>" "\"*/\" 'n>" "\"public \" (s type)" "(if (string= \"boolean\" (jde-gen-lookup-named 'type) ) " "\" is\" " "\" get\" ) " "(jde-gen-init-cap (jde-gen-lookup-named 'name))" "\"() {return \" (s name) \";}\" 'n> 'n>" "\"/**\" 'n>" "\"* Set the value of \" (s name) \".\" 'n>" "\"* @param v  Value to assign to \" (s name) \".\" 'n>" "\"*/\" 'n>" "\"public void set\" (jde-gen-init-cap (jde-gen-lookup-named 'name))" "\"(\" (s type) \"  v) {this.\" (s name) \" = v;}\" 'n>")))
 '(jde-db-option-application-args nil)
 '(jde-run-option-verify (quote (nil t)))
 '(jde-bug-server-shmem-name (quote (t . "JDEbug")))
 '(jde-javadoc-describe-interface-template (quote ("* Describe interface " (jde-javadoc-code name) " here.")))
 '(jde-db-option-classpath nil)
 '(jde-bug-vm-executable (quote ("java")))
 '(jde-gen-k&r t)
 '(jde-gen-cflow-if (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"if\")" "'(l > \"if (\" (p \"if-clause: \" clause) \") \"" "\"{\" > n> r n" "\"} // end of if (\" (s clause) \")\" > n>)" ")")))
 '(jde-db-option-heap-profile (quote (nil "./java.hprof" 5 20 "Allocation objects")))
 '(jde-run-option-stack-size (quote ((1 . "megabytes") (1 . "megabytes"))))
 '(jde-gen-cflow-case (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"case\")" "'(l n \"case \" (p \"value: \") \":\" > n> p n" "\"break;\" > n> p)" ")")))
 '(jde-jdk-doc-url "http://www.javasoft.com/products/jdk/1.2/docs/index.html")
 '(jde-gen-console-buffer-template (quote ("(funcall jde-gen-boilerplate-function) 'n" "\"/**\" 'n" "\" * \"" "(file-name-nondirectory buffer-file-name) 'n" "\" *\" 'n" "\" *\" 'n" "\" * Created: \" (current-time-string) 'n" "\" *\" 'n" "\" * @author \" (user-full-name) 'n" "\" * @version\" 'n" "\" */\" 'n>" "'n>" "\"public class \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" {\"  'n>" "" "\"public \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" ()\"" "\" {\"  'n>" "" "'n>" "\"}\" 'n>" "'n>" "\"public static void main(String[] args)\"" "\" {\"  'n>" "" "'p 'n>" "\"}\" 'n> 'n>" "\"} // \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "'n>")))
 '(jde-run-option-vm-args nil)
 '(jde-gen-mouse-motion-listener-template (quote ("'& (P \"Component name: \")" "\".addMouseMotionListener(new MouseMotionAdapter() {\" 'n>" "\"public void mouseDragged(MouseEvent e) {}\" 'n>" "\"public void mouseMoved(MouseEvent e) {}});\" 'n>")))
 '(jde-bug-window-message nil)
 '(jde-gen-cflow-if-else (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"ife\")" "'(l > \"if (\" (p \"if-clause: \" clause) \") \"" "\"{\" > n> r n" "\"} // end of if (\" (s clause) \")\" > n>" "> \"else \"" "\"{\" > n> r n" "\"} // end of if (\" (s clause) \")else\" > n>)" ")")))
 '(jde-bug-jpda-directory "")
 '(jde-gen-property-change-support (quote ("'&" "\"protected PropertyChangeSupport pcs =  new PropertyChangeSupport(this);\" 'n>" "\"/**\" 'n>\"* Adds a PropertyChangeListener to the listener list.\" 'n>" "\"* The listener is registered for all properties.\" 'n>" "\"*\" 'n> \"* @param listener The PropertyChangeListener to be added\" 'n> \"*/\" 'n>" "\"public void addPropertyChangeListener(PropertyChangeListener listener) {\" 'n>" "\"pcs.addPropertyChangeListener(listener);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Removes a PropertyChangeListener from the listener list.\" 'n>" "\"* This removes a PropertyChangeListener that was registered for all properties.\" 'n>" "\"*\" 'n> \"* @param listener The PropertyChangeListener to be removed\" 'n> \"*/\" 'n>" "\"public void removePropertyChangeListener(PropertyChangeListener listener) {\" 'n>" "\"pcs.removePropertyChangeListener(listener);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Adds a PropertyChangeListener for a specific property.\" 'n>" "\"* The listener will be invoked only when a call on firePropertyChange\" 'n>" "\"* names that specific property.\" 'n>" "\"*\" 'n> \"* @param propertyName The name of the property to listen on\" 'n>" "\"* @param listener The PropertyChangeListener to be added\" 'n> \"*/\" 'n>" "\"public void addPropertyChangeListener(String propertyName,\" 'n>" "\"PropertyChangeListener listener) {\" 'n>" "\"pcs.addPropertyChangeListener(propertyName, listener);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Removes a PropertyChangeListener for a specific property.\" 'n>" "\"*\" 'n> \"* @param propertyName The name of the property that was listened on\" 'n>" "\"* @param listener The PropertyChangeListener to be removed\" 'n> \"*/\" 'n>" "\"public void removePropertyChangeListener(String propertyName,\" 'n>" "\"PropertyChangeListener listener) {\"  'n>" "\"pcs.removePropertyChangeListener(propertyName, listener);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Reports a bound property update to any registered listeners. \" 'n>" "\"* No event is fired if old and new are equal and non-null.\" 'n>" "\"*\" 'n> \"* @param propertyName The programmatic name of the property that was changed\" 'n>" "\"* @param oldValue The old value of the property\" 'n>" "\"* @param newValue The new value of the property.\" 'n> \"*/\" 'n>" "\"public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {\" 'n>" "\"pcs.firePropertyChange(propertyName, oldValue, newValue);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Reports a bound property update to any registered listeners. \" 'n>" "\"* No event is fired if old and new are equal and non-null.\" 'n>" "\"* This is merely a convenience wrapper around the more general\" 'n>" "\"* firePropertyChange method that takes Object values.\" 'n>" "\"* No event is fired if old and new are equal and non-null.\" 'n>" "\"*\" 'n> \"* @param propertyName The programmatic name of the property that was changed\" 'n>" "\"* @param oldValue The old value of the property\" 'n>" "\"* @param newValue The new value of the property.\" 'n> \"*/\" 'n>" "\"public void firePropertyChange(String propertyName, int oldValue, int newValue) {\" 'n>" "\"pcs.firePropertyChange(propertyName, oldValue, newValue);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Reports a bound property update to any registered listeners. \" 'n>" "\"* No event is fired if old and new are equal and non-null.\" 'n>" "\"* This is merely a convenience wrapper around the more general\" 'n>" "\"* firePropertyChange method that takes Object values.\" 'n>" "\"* No event is fired if old and new are equal and non-null.\" 'n>" "\"*\" 'n> \"* @param propertyName The programmatic name of the property that was changed\" 'n>" "\"* @param oldValue The old value of the property\" 'n>" "\"* @param newValue The new value of the property.\" 'n> \"*/\" 'n>" "\"public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {\" 'n>" "\"pcs.firePropertyChange(propertyName, oldValue, newValue);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Fires an existing PropertyChangeEvent to any registered listeners.\" 'n>" "\"* No event is fired if the given event's old and new values are equal and non-null. \" 'n>" "\"*\" 'n> \"* @param evt The PropertyChangeEvent object.\" 'n>\"*/\" 'n>" "\"public void firePropertyChange(PropertyChangeEvent evt) {\" 'n>" "\"pcs.firePropertyChange(evt);\" 'n> \"}\" 'n> 'n>" "\"/**\" 'n>\"* Checks if there are any listeners for a specific property.\" 'n>" "\"*\" 'n> \"* @param evt The PropertyChangeEvent object.\" 'n>" "\"* @return <code>true</code>if there are one or more listeners for the given property\" 'n>" "\"*/\" 'n>" "\"public boolean hasListeners(String propertyName) {\" 'n>" "\"return pcs.hasListeners(propertyName);\" 'n> \"}\" 'n> 'n>")))
 '(jde-read-make-args nil)
 '(jde-gen-entity-bean-template (quote ("(jde-wiz-insert-imports-into-buffer (list \"javax.ejb.*\"
\"java.rmi.RemoteException\"))" "(jde-wiz-update-implements-clause \"EntityBean\")" "'> \"public void ejbActivate() throws RemoteException {\"'n> \"}\"'n
'n" "'> \"public void ejbPassivate() throws RemoteException {\"'n> \"}\"'n
'n" "'> \"public void ejbLoad() throws RemoteException {\"'n>\"}\"'n 'n" "'> \"public void ejbStore() throws RemoteException {\"'n>\"}\"'n 'n" "'> \"public void ejbRemove() throws RemoteException {\"'n>\"}\"'n 'n" "'> \"public void setEntityContext(EntityContext ctx) throws
RemoteException {\"" "'n>\"}\"'n 'n" "'> \"public void unsetEntityContext() throws RemoteException {\"'n>
\"}\"'n> 'n")))
 '(jde-run-classic-mode-vm nil)
 '(jde-db-option-verify (quote (nil t)))
 '(jde-bug-saved-breakpoints nil)
 '(jde-bug-debug nil)
 '(jde-project-file-name "prj.el")
 '(jde-run-java-vm "java")
 '(jde-compile-option-directory "~/cs555/javarun")
 '(jde-run-applet-doc "")
 '(jde-db-option-properties nil)
 '(jde-gen-cflow-for-i (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"fori\")" "'(l > \"for (int \" (p \"variable: \" var) \" = 0; \"" "(s var)" "\" < \"(p \"upper bound: \" ub)\"; \" (s var) \"++) \"" "\"{\" > n> r n" "\"} // end of for (int \" (s var) \" = 0; \"" "(s var) \" < \" (s ub) \"; \" (s var) \"++)\" > n>)" ")")))
 '(jde-wiz-insert-excluded-packages-regexp "bsh.*")
 '(jde-read-compile-args nil)
 '(jde-db-read-app-args nil)
 '(jde-compile-option-vm-args (quote ("-mx48m")))
 '(jde-compile-option-depend nil)
 '(jde-gen-cflow-switch (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"switch\")" "'(l > \"switch (\" (p \"switch-condition: \" clause) \") \"" "\"{\" > n" "\"case \" (p \"first value: \") \":\" > n> p n" "\"break;\" > n> p n" "\"default:\" > n> p n" "\"break;\" > n" "\"} // end of switch (\" (s clause) \")\" > n>)" ")")))
 '(jde-compile-option-nowarn nil)
 '(jde-bug-raise-frame-p t)
 '(jde-run-option-properties nil)
 '(jde-javadoc-param-tag-template (quote ("* @param " name " " (jde-javadoc-a type) " " (jde-javadoc-code type) " value")))
 '(jde-db-option-garbage-collection (quote (t t)))
 '(jde-key-bindings (quote (("[? ? ?]" . jde-run-menu-run-applet) ("[? ? ?]" . jde-build) ("[? ? ?]" . jde-compile) ("[? ? ?]" . jde-debug) ("[? ? ?]" . jde-wiz-implement-interface) ("[? ? ?j]" . jde-javadoc-generate-javadoc-template) ("[? ? ?]" . bsh) ("[? ? ?]" . jde-gen-println) ("[? ? ?]" . jde-browse-jdk-doc) ("[? ? ?]" . jde-save-project) ("[? ? ?]" . jde-wiz-update-class-list) ("[? ? ?]" . jde-run) ("[? ? ?]" . speedbar-frame-mode) ("[? ? ?]" . jde-db-menu-debug-applet) ("[? ? ?]" . jde-help-symbol) ("[? ? ?]" . jde-show-class-source) ("[? ? ?]" . jde-wiz-find-and-import) ("[(control c) (control v) (control ?.)]" . jde-complete-at-point-menu) ("[(control c) (control v) ?.]" . jde-complete-at-point))))
 '(jde-db-mode-hook nil)
 '(jde-project-context-switching-enabled-p t)
 '(jde-javadoc-describe-field-template (quote ("* Describe " (jde-javadoc-field-type modifiers) " " (jde-javadoc-code name) " here.")))
 '(jde-db-option-heap-size (quote ((1 . "megabytes") (16 . "megabytes"))))
 '(jde-bug-key-bindings (quote (("[? ? ?]" . jde-bug-step-over) ("[? ? ?]" . jde-bug-step-into) ("[? ? ?]" . jde-bug-step-out) ("[? ? ?]" . jde-bug-continue) ("[? ? ?]" . jde-bug-set-breakpoint))))
 '(jde-run-option-verbose (quote (nil nil nil)))
 '(jde-bug-jdk-directory "e:/jdk1.3/")
 '(jde-bug-breakpoint-cursor-colors (quote ("cyan" . "brown")))
 '(jde-global-classpath (quote ("/home/swamp2/maida/cxg9789/cs555/javarun")))
 '(jde-bug-vm-includes-jpda-p nil)
 '(jde-javadoc-describe-class-template (quote ("* Describe class " (jde-javadoc-code name) " here.")))
 '(jde-run-application-class "PhaseSegregator.Network")
 '(jde-gen-cflow-else (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"else\")" "'(l > \"else \"" "\"{\" > n> r n" "\"} // end of else\" > n>)" ")")))
 '(jde-compile-option-target (quote ("1.2")))
 '(jde-imenu-enable t)
 '(jde-make-program "make")
 '(jde-db-option-vm-args nil)
 '(jde-gen-window-listener-template (quote ("'& (P \"Window name: \")" "\".addWindowListener(new WindowAdapter() {\" 'n>" "\"public void windowActivated(WindowEvent e) {}\" 'n>" "\"public void windowClosed(WindowEvent e) {}\" 'n>" "\"public void windowClosing(WindowEvent e) {System.exit(0);}\" 'n>" "\"public void windowDeactivated(WindowEvent e) {}\" 'n>" "\"public void windowDeiconified(WindowEvent e) {}\" 'n>" "\"public void windowIconified(WindowEvent e) {}\" 'n>" "\"public void windowOpened(WindowEvent e) {}});\" 'n>")))
 '(jde-run-option-java-profile (quote (nil . "./java.prof")))
 '(jde-entering-java-buffer-hooks (quote (jde-reload-project-file)))
 '(jde-db-option-java-profile (quote (nil . "./java.prof")))
 '(jde-bug-server-socket (quote (t . "2112")))
 '(jde-javadoc-author-tag-template (quote ("* @author <a href=\"mailto:" user-mail-address "\">" user-full-name "</a>")))
 '(jde-bug-debugger-command-timeout 10)
 '(jde-run-read-app-args nil)
 '(jde-run-option-heap-size (quote ((1 . "megabytes") (60 . "megabytes"))))
 '(jde-db-option-verbose (quote (nil nil nil)))
 '(jde-compile-option-debug (quote ("all" (t t t))))
 '(jde-javadoc-return-tag-template (quote ("* @return " (jde-javadoc-a type) " " (jde-javadoc-code type) " value")))
 '(jde-run-working-directory "~/cs555/javarun")
 '(jde-compile-option-bootclasspath nil)
 '(jde-db-read-vm-args nil)
 '(jde-compile-option-extdirs nil)
 '(jde-compiler "javac")
 '(jde-appletviewer-option-encoding "")
 '(jde-gen-session-bean-template (quote ("(jde-wiz-insert-imports-into-buffer (list \"javax.ejb.*\"
\"java.rmi.RemoteException\"))" "(jde-wiz-update-implements-clause \"SessionBean\")" "'> \"public void ejbActivate() throws RemoteException {\"'n> \"}\"'n
'n" "'> \"public void ejbPassivate() throws RemoteException {\"'n> \"}\"'n
'n" "'> \"public void ejbRemove() throws RemoteException {\"'n> \"}\"'n 'n" "'> \"public void setSessionContext(SessionContext ctx) throws
RemoteException {\"" "'n> \"}\"'n 'n" "'> \"public void unsetSessionContext() throws RemoteException {\"'n>
\"}\"'n 'n")))
 '(jde-gen-cflow-main (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"main\")" "'(l > \"public static void main (String[] args) \"" "\"{\" > n> r n" "\"} // end of main ()\" > n>)" ")")))
 '(jde-javadoc-describe-constructor-template (quote ("* Creates a new " (jde-javadoc-code name) " instance.")))
 '(jde-db-option-stack-size (quote ((128 . "kilobytes") (400 . "kilobytes"))))
 '(jde-run-option-garbage-collection (quote (t t)))
 '(jde-db-set-initial-breakpoint t)
 '(jde-gen-cflow-for (quote ("(if (jde-parse-comment-or-quoted-p)" "'(l \"for\")" "'(l > \"for (\" (p \"for-clause: \" clause) \") \"" "\"{\" > n> r n" "\"} // end of for (\" (s clause) \")\" > n>)" ")")))
 '(jde-use-font-lock t))
