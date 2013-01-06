package event;

import java.io.File;

import fr.swampwolf.events.Event;

public class FileLoadRequest extends Event {
	
	private File file;

	public FileLoadRequest(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}

}
