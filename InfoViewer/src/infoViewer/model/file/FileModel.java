package infoViewer.model.file;

import java.util.ArrayList;

import infoViewer.view.Observer;

public class FileModel {

	private ArrayList<Observer> observers = new ArrayList();

	private String text;
	private String name = null;

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
		notifyObservers();
	}

	public void notifyObservers() {

		for (Observer observer : observers)
			observer.update(null, null);
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void addObserver(Observer observer) {

		observers.add(observer);
	}
}
