// "Transform body to single exit-point form" "true"
class Test {
    int <caret>test(String s) {
        if(s == null) {
            if (Math.random() > 0.5) return 2;
            return 4;
        }
        if(s.isEmpty()) return 3;
        System.out.println(s);
        return 1;
    }
}