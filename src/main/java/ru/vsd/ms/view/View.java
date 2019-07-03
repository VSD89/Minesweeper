package ru.vsd.ms.view;

import ru.vsd.ms.controller.Controller;
import ru.vsd.ms.dto.CellDto;
import ru.vsd.ms.dto.FieldDto;
import ru.vsd.ms.highscores.Record;
import ru.vsd.ms.model.CellState;
import ru.vsd.ms.level.GameLevel;
import ru.vsd.ms.observer.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class View implements Observer {
    private Controller controller;
    private JFrame mainFrame;
    private JFrame recordFrame;
    private JPanel fieldPanel;
    private JPanel recordPanel;
    private JMenuBar menuBar;
    private JComboBox levelComboBox;
    private JButton[][] buttons;
    private JLabel timer;
    private int viewWidth;
    private int viewLength;
    private GameLevel level;
    private static final String JUNIOR_STRING = "Новичок";
    private static final String MIDDLE_STRING = "Любитель";
    private static final String SENIOR_STRING = "Профессионал";

    public View(Controller controller) {
        this.controller = controller;
        mainFrame = new JFrame();
        mainFrame.setTitle("Сапер");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(330, 330);//
        mainFrame.setResizable(false);
        fieldPanel = new JPanel();
        JButton startSameGame = new JButton();
        startSameGame.setPreferredSize(new Dimension(39, 39));
        startSameGame.setIcon(getIcon("mine"));

        startSameGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(startSameGame)) {
                    controller.newSameGame();
                }
            }
        });

        mainFrame.add(BorderLayout.CENTER, fieldPanel);
        controller.newGame(GameLevel.JUNIOR);
        startGame(new FieldDto(9, 9));
        menuBar = new JMenuBar();
        JMenu newGameMenu = new JMenu("Новая игра");
        JMenuItem newJuniorGame = new JMenuItem(JUNIOR_STRING);
        JMenuItem newMiddleGame = new JMenuItem(MIDDLE_STRING);
        JMenuItem newSeniorGame = new JMenuItem(SENIOR_STRING);
        JMenuItem newCustomGame = new JMenuItem("Настроить");
        JMenu recordsMenu = new JMenu("Рекорды");
        JMenuItem seeRecords = new JMenuItem("Посмотреть");
        timer = new JLabel("       Время: ");

        newJuniorGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(mainFrame.getJMenuBar().getMenu(0).getItem(0))) {
                    level = GameLevel.JUNIOR;
                    controller.newGame(level);
                }
            }
        });

        newMiddleGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(mainFrame.getJMenuBar().getMenu(0).getItem(1))) {
                    level = GameLevel.MIDDLE;
                    controller.newGame(level);
                }
            }
        });

        newSeniorGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(mainFrame.getJMenuBar().getMenu(0).getItem(2))) {
                    level = GameLevel.SENIOR;
                    controller.newGame(level);
                }
            }
        });

        newCustomGame.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(mainFrame.getJMenuBar().getMenu(0).getItem(3))) {
                    level = GameLevel.CUSTOM;
                    initCustomMode();
                }
            }
        });

        seeRecords.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource().equals(mainFrame.getJMenuBar().getMenu(1).getItem(0))) {
                    recordsMenuHandler();
                }
            }
        });

        newGameMenu.add(newJuniorGame);
        newGameMenu.add(newMiddleGame);
        newGameMenu.add(newSeniorGame);
        newGameMenu.add(newCustomGame);
        menuBar.add(newGameMenu);
        recordsMenu.add(seeRecords);
        menuBar.add(recordsMenu);
        menuBar.add(startSameGame);
        menuBar.add(timer);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    @Override
    public void update(CellDto cell) {
        if (cell.getState().equals(CellState.OPENED)) {
            if (cell.isMined()) {
                buttons[cell.getRow()][cell.getColumn()].setIcon(getIcon("bombed"));
            } else {
                buttons[cell.getRow()][cell.getColumn()].setIcon(getIcon(String.valueOf(cell.getCounter())));
            }
        } else {
            buttons[cell.getRow()][cell.getColumn()].setIcon(getIcon(String.valueOf(cell.getState())));
        }
    }

    @Override
    public void startGame(FieldDto field) {
        mainFrame.setTitle("Сапер");
        viewWidth = field.getFieldWidth();
        viewLength = field.getFieldLength();
        fieldPanel.removeAll();
        GridLayout grid = new GridLayout(viewWidth, viewLength);
        fieldPanel.setLayout(grid);
        buttons = new JButton[viewWidth][viewLength];
        MineListener mineListener = new MineListener();
        for (int i = 0; i < viewWidth; i++) {
            for (int j = 0; j < viewLength; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(40, 40));
                buttons[i][j].setIcon(getIcon("closed"));
                buttons[i][j].addMouseListener(mineListener);
                fieldPanel.add(buttons[i][j]);
            }
        }
        mainFrame.pack();
    }

    @Override
    public void finishGame(CellDto cell) {
        int m = cell.getRow();
        int n = cell.getColumn();
        mainFrame.setTitle("Игра окончена");
        buttons[m][n].setIcon(getIcon("redbombed"));
        for (int i = 0; i < viewWidth; i++) {
            for (int j = 0; j < viewLength; j++) {
                controller.openCell(i, j);
            }
        }
        JOptionPane.showMessageDialog(mainFrame, "Игра окончена");
    }

    @Override
    public void winGame() {
        mainFrame.setTitle("Вы победили");
        JOptionPane.showMessageDialog(mainFrame, "Вы победили");
    }

    @Override
    public void printTime(long time) {
        timer.setText("       Время: " + time);
        mainFrame.pack();
    }

    @Override
    public void recordTime(long time) {
        JFrame setRecordFrame = new JFrame("Новый рекорд");
        JPanel setRecordPanel = new JPanel();
        JLabel setRecordLabel = new JLabel("Вы установили новый рекорд. Введите ваше имя");
        JTextField nameField = new JTextField(10);
        JButton okButton = new JButton("Ок");
        setRecordFrame.setResizable(false);
        mainFrame.setEnabled(false);

        okButton.addActionListener(e1 -> {
            mainFrame.setEnabled(true);
            setRecordFrame.dispose();
            controller.addHighScore(time, nameField.getText());
        });

        setRecordFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setEnabled(true);
            }
        });

        setRecordPanel.add(setRecordLabel);
        setRecordPanel.add(nameField);
        setRecordPanel.add(okButton);
        setRecordFrame.add(setRecordPanel);
        setRecordFrame.pack();
        setRecordFrame.setLocationRelativeTo(null);
        setRecordFrame.setVisible(true);
    }

    @Override
    public void stopTimer() {
        controller.stopTimer();
    }

    @Override
    public void startTimer() {
        controller.startTimer();
    }

    @Override
    public void showHighScores(GameLevel level) {
        controller.readHighScores(level);
    }

    @Override
    public void updateHighScores(List<Record> scores, GameLevel watchingLevel) {
        if (recordFrame == null) {
            initRecordWindow();
        } else {
            switch (watchingLevel) {
                case JUNIOR:
                    levelComboBox.setSelectedItem(JUNIOR_STRING);
                    break;
                case MIDDLE:
                    levelComboBox.setSelectedItem(MIDDLE_STRING);
                    break;
                case SENIOR:
                    levelComboBox.setSelectedItem(SENIOR_STRING);
                    break;
                default:
                    break;
            }
            recordFrame.setVisible(true);
        }
        updateRecordWindow(scores);
    }

    private void recordsMenuHandler() {
        if (recordFrame == null) {
            showHighScores(GameLevel.JUNIOR);
        } else {
            levelComboBox.setSelectedItem(JUNIOR_STRING);
            recordFrame.setVisible(true);
        }
    }

    private void initCustomMode() {
        JFrame paramFrame = new JFrame("Настройка");
        JPanel paramPanel = new JPanel();
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(9, 1, 16, 1));
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(9, 1, 33, 1));
        JSpinner mineSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 527, 1));
        JLabel widthLabel = new JLabel("Ширина");
        JLabel lengthLabel = new JLabel("Длина");
        JLabel mineLabel = new JLabel("Количество мин");
        JButton okButton = new JButton("Ок");
        paramFrame.setResizable(false);
        mainFrame.setEnabled(false);

        okButton.addActionListener(e1 -> {
            int width = (Integer) widthSpinner.getValue();
            int length = (Integer) lengthSpinner.getValue();
            int mines = (Integer) mineSpinner.getValue();
            controller.newGame(width, length, mines);
            mainFrame.setEnabled(true);
            paramFrame.dispose();
        });

        paramFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setEnabled(true);
            }
        });

        paramPanel.add(widthLabel);
        paramPanel.add(widthSpinner);
        paramPanel.add(lengthLabel);
        paramPanel.add(lengthSpinner);
        paramPanel.add(mineLabel);
        paramPanel.add(mineSpinner);
        paramPanel.add(okButton);
        paramFrame.add(paramPanel);
        paramFrame.pack();
        paramFrame.setLocationRelativeTo(null);
        paramFrame.setVisible(true);
    }

    private void initRecordWindow() {
        recordFrame = new JFrame("Рекорды");
        recordPanel = new JPanel();
        recordFrame.setSize(500, 300);
        recordFrame.setResizable(false);
        mainFrame.setEnabled(false);

        recordFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setEnabled(true);
            }
        });

        String[] levels = {JUNIOR_STRING, MIDDLE_STRING, SENIOR_STRING};
        levelComboBox = new JComboBox(levels);
        levelComboBox.addActionListener(e -> {
            JComboBox box = (JComboBox) e.getSource();
            String item = (String) box.getSelectedItem();
            switch (item) {
                case (JUNIOR_STRING):
                    showHighScores(GameLevel.JUNIOR);
                    break;
                case (MIDDLE_STRING):
                    showHighScores(GameLevel.MIDDLE);
                    break;
                case (SENIOR_STRING):
                    showHighScores(GameLevel.SENIOR);
                    break;
                default:
                    showHighScores(GameLevel.JUNIOR);
                    break;
            }
        });

        recordPanel.add(levelComboBox);
        recordFrame.add(recordPanel);
        recordFrame.setLocationRelativeTo(null);
        recordFrame.setVisible(true);
    }

    private void updateRecordWindow(List<Record> scores) {
        String[] columnNames = {"Место", "Имя", "Время"};
        int length = scores.size();
        String[][] users = new String[length][3];
        for (int i = 0; i < length; i++) {
            String[] thisUser = {String.valueOf(i + 1), scores.get(i).getName(), String.valueOf(scores.get(i).getTime())};
            users[i] = thisUser;
        }
        JTable table = new JTable(users, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        if (recordPanel.getComponentCount() > 1) {
            recordPanel.remove(1);
        }
        recordPanel.add(scrollPane);
        recordFrame.pack();
    }

    public ImageIcon getIcon(String iconName) {
        return new ImageIcon(getClass().getResource("/images/" + iconName + ".png"));
    }

    private class MineListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            for (int i = 0; i < viewWidth; i++) {
                for (int j = 0; j < viewLength; j++) {
                    if ((e.getSource().equals(buttons[i][j])) && (e.getButton() == MouseEvent.BUTTON1)) {
                        controller.openCell(i, j);
                    } else if ((e.getSource().equals(buttons[i][j])) && (e.getButton() == MouseEvent.BUTTON3)) {
                        controller.markCell(i, j);
                    }
                }
            }
        }
    }
}
