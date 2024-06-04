package com.example.Tambola;

import java.util.*;
import java.util.stream.Collectors;

public class TambolaTicket {
    public static class Counter {
        public long value = 0L;
    }

    public static class GenState {
        public long counter = 0L;
        public List<List<Integer>> numbers;
        public List<Integer> columns;
    }

    private final int defaultCount;
    private final Random random;

    public TambolaTicket(int defCount) {
        this.defaultCount = defCount;
        this.random = new Random();
    }

    public List<List<Integer>> generateAndPrintTickets() {
        List<List<Integer>>totalTickets=generateAndPrintTickets(defaultCount);
        return totalTickets;
    }

    private List<Integer> generateSeries(int min, int max) {
        ArrayList<Integer> items = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            items.add(i);
        }
        return items;
    }

    private <T> List<T> generateFilledArray(int size, T fill) {
        ArrayList<T> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(fill);
        }
        return items;
    }

    private List<List<Integer>> generateNumSet() {
        List<Integer> columns = generateSeries(0,8);
        List<List<Integer>> nums = new ArrayList<>();
        for (Integer n: columns) {
            int add = n * 10;
            int minAdd = add;
            List<Integer> col = new ArrayList<>();
            if (n == 0) {
                col.add(90);
                minAdd = 1;
            }
            col.addAll(generateSeries(minAdd , 9 + add));
            nums.add(col);
        }
        System.out.println(nums);
        return nums;
    }

    public List<List<Integer>> generateAndPrintTickets(int count) {
        List<List<Integer>> tickets = new ArrayList<>();
        long millis = System.currentTimeMillis();
        GenState state = new GenState();
        state.numbers = generateNumSet();
        state.columns = generateSeries(0, 8);
        for (int i = 0; i < count; i++) {
            if (i % 6 == 0) state.numbers = generateNumSet();
            tickets.add(generateTicketSequence(state));
        }
        double diff = (System.currentTimeMillis() - millis) / 1000.0;
        System.out.println("took " + diff + "sec");
        for (List<Integer> ticket : tickets) {
            System.out.println(ticket);
        }
        return tickets;
    }

    public void wrapperLine() {
        System.out.println("+" + String.join("+", generateFilledArray(9, "--")) + "+");
    }

    public void printRow(List<Integer> nums) {
        System.out.println("|" + nums.stream().map(a -> {
            if (a == 0) return "  ";
            return String.format("%2s", a.toString());
        }).collect(Collectors.joining("|")) + "|");
    }


    public void printAsGrid(List<Integer> nums) {
        List<Integer> filledTicket = generateFilledArray(27, 0);
        for (int i = 0; i < 15; i++) {
            int row = Math.floorDiv(i, 5);
            int col = Math.floorDiv(nums.get(i), 10);
            if (col == 9) col = 8;
            filledTicket.set(row * 5 + col, nums.get(i));
        }
        columnSort(filledTicket);
        wrapperLine();
        printRow(filledTicket.subList(0,9));
        wrapperLine();
        printRow(filledTicket.subList(9,18));
        wrapperLine();
        printRow(filledTicket.subList(18,27));
        wrapperLine();
    }

    private void columnSort(List<Integer> items) {
        for (int i = 0; i < 9; i++) {
            Integer n1 = items.get(i);
            Integer n2 = items.get(i + 9);
            Integer n3 = items.get(i + 18);
            if (n1 == 0 && n2 == 0 && n3 == 0) continue;
            if (n1 !=0 && n2 != 0 && n3 != 0) {
                List<Integer> vars = new ArrayList<>(Arrays.asList(n1, n2, n3));
                Collections.sort(vars);
                items.set(i, vars.get(0));
                items.set(i + 9, vars.get(1));
                items.set(i + 18, vars.get(2));
                continue;
            }
            if (n1 != 0 && n2 != 0) {
                List<Integer> vars = new ArrayList<>(Arrays.asList(n1, n2));
                Collections.sort(vars);
                items.set(i, vars.get(0));
                items.set(i + 9, vars.get(1));
                continue;
            }
            if (n1 != 0 && n3 != 0) {
                List<Integer> vars = new ArrayList<>(Arrays.asList(n1, n3));
                Collections.sort(vars);
                items.set(i, vars.get(0));
                items.set(i + 18, vars.get(1));
                continue;
            }
            if (n2 != 0 && n3 != 0) {
                List<Integer> vars = new ArrayList<>(Arrays.asList(n2, n3));
                Collections.sort(vars);
                items.set(i + 9, vars.get(0));
                items.set(i + 18, vars.get(1));
            }
        }
    }

    private Integer getSample(List<Integer> nums) {
        if (nums.isEmpty()) return null;
        int index = random.nextInt(nums.size());
        return nums.remove(index);
    }

    private List<Integer> generateTicketSequence(GenState s) {
        List<Integer> ticket = new ArrayList<>();
        List<Integer> line = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (s.counter % 9 == 0) s.columns = generateSeries(0,8);
            s.counter = s.counter + 1;
            if (i % 5 == 0) line = new ArrayList<>();
            Integer column = getSample(s.columns);
            List<Integer> tempC = new ArrayList<>();
            while (line.contains(column)) {
                tempC.add(column);
                column = getSample(s.columns);
            }

            s.columns.addAll(tempC);
            if (column == null) continue;
            line.add(column);
            Integer sample = getSample(s.numbers.get(column));
            if (sample != null) ticket.add(sample);
        }
        System.out.println(ticket);
        return ticket;
    }
}