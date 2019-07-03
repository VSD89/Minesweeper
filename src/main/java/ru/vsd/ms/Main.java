package ru.vsd.ms;

import ru.vsd.ms.controller.Controller;
import ru.vsd.ms.model.Model;
import ru.vsd.ms.view.View;

public class Main {

    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        View view = new View(controller);
        model.registerObserver(view);
    }
}
