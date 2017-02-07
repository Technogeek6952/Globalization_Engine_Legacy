
public class REFERENCE {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	
	/*--------Code--------------------------*/
	
	//Generic todos that don't belong in any one file
	//TODO: make launcher that allows selection of plugins to be loaded, and checks for updates
	//FIXME: rare startup error (see comment below in REFERENCE.java)
	//Sometimes when launching a rare error (probably related to threads and not checking locks, or something) 
	//causes the engine to not be set up correctly, and nothing will happen except an exception for buffers not being created
	//unfortunately I cannot seem to replicate this error reliably. It also appears to be very rare.
	
	//TODO: some sort of world history system, so that the user can press a button (esc?) to go to the last screen
	//add an interface to the world.java file that can be added as a listener to any world. Would report keys pressed if on that
	//screen. if the key is esc (or other), go back in history, or possibly load pause menu
}
