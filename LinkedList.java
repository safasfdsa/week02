public class LinkedList<T> {
    private static int size;
    private Node<T> first;
    private Node<T> last;

    private static class Node<T> {
        private T data;
        private Node<T> pri;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Node<T> getPri() {
            return pri;
        }

        public void setPri(Node<T> pri) {
            this.pri = pri;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

    }

    public static int getSize() {
        return size;
    }

    /**
     * ��ӽڵ㵽ͷ��
     * */
    public boolean addFirst(T value){
        Node<T> node = new Node<>(value);
        if (size == 0) {
            first = node;
            last = first;
        }else {
            node.next = first;
            first.pri = node;
            first = node;
        }
        size++;
        return true;
    }
    /**
     * ��ӽڵ㵽β��
     * */
    public boolean addLast(T value){
        if (size == 0){
            return addFirst(value);
        }else {
            Node<T> node = new Node<>(value);
            last.next = node;
            node.pri = last;
            last = node;
        }
        size++;
        return true;
    }
    /**
     * Ԫ����ӵ�ָ��λ��
     * */
    public boolean add(int index,T value){
        if (index < 0 || index > size){
            throw new IndexOutOfBoundsException("�����±�Խ�� Index:" + index + "\tsize:" + size);
        } else if(index == 0){
            return addFirst(value);
        }else if (index == size){
            return addLast(value);
        }else {
            Node<T> node = new Node<>(value);
            Node<T> head = first;
            for (int i = 0; i < index-1; i++) {
                head = head.getNext();
            }
            node.next = head.next;
            head.next = node;
            node.pri = head;
            node.next.pri = node;
        }
        size++;
        return true;
    }

    /**
     * ɾ��ͷ���
     * */
    public T removeFirst(){
        if (size == 0){
            throw new RuntimeException("����Ϊ�գ�");
        }
        T data = first.getData();
        Node<T> node = first.next;
        node.setPri(null);
        first = node;
        return data;
    }

    /**
     * ɾ��β�ڵ�
     * */
    public T removeLast(){
        if (size == 0){
            throw new RuntimeException("����Ϊ�գ�");
        }
        T data = last.getData();
        Node<T> node = last.pri;
        node.setNext(null);
        last = node;
        return data;
    }

    /**
     * ɾ��ָ���±���
     * */
    public T remove(int index){
        if (size == 0){
            throw new RuntimeException("����Ϊ�գ�");
        }
        //ע����ӵ�ʱ���±�ȡ����size��������ӵ�λ�ÿ�����size������ɾ����ʱ����
        if (index < 0 || index > size-1){
            throw new IndexOutOfBoundsException("�����±�Խ�� Index:" + index + "\tsize:" + size);
        } else if(index == 0){
            return removeFirst();
        }else {
            Node<T> node = first;
            for (int i = 0; i < index - 1; i++) {
                node = node.next;
            }
            Node<T> temp = node.next;
            if (temp != last){
                T data = temp.getData();
                node.next = temp.next;
                temp.next.pri = node;
                temp.setNext(null);
                return data;
            }else {
                return removeLast();
            }
        }
    }

    /**
     * ��ȡ��Ӧ�±�����
     * */
    public T getData(int index){
        if (index<0 || index>size-1){
            throw new IndexOutOfBoundsException("�����±�Խ�� Index:" + index + "\tsize:" + size);
        }else if (size == 0){
            throw new RuntimeException("����Ϊ��");
        }else if (size == 1){
            return first.data;
        }else {
            Node<T> node = first;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            return node.data;
        }
    }

    /**
     * �������
     * */
    public void clear(){
        first.next = null;
        last = first;
    }

    /**
     * ���������ǰ��������
     * */
    public void print(){
        if (size == 0) {
            System.out.println("������Ϊ��!");
        }
        Node<T> node = first;
        while (node != null) {
            System.out.print(node.getData() + "\t");
            node = node.next;
        }
        System.out.println();
    }

    /**
     * ��ϸ�������:
     *      ǰ���ڵ�ֵ
     *      ��ǰ�ڵ�ֵ
     *      ��̽ڵ�ֵ
     * */
    public void detailPrint(){
        if (size == 0) {
            System.out.println("������Ϊ��!");
        }
        Node<T> node = first;
        while (node != null) {
            System.out.print("ǰ���ڵ�ֵ��");
            System.out.printf("%-5s",node.pri == null ? "null\t" : node.pri.getData()+"\t");
            System.out.print("��ǰ�ڵ�ֵ��");
            System.out.printf("%-6s",node.getData() + "\t");
            System.out.print("��̽ڵ�ֵ��");
            System.out.printf("%-5s",node.next == null ? "null\t" : node.next.getData()+"\t");
            System.out.println();
            node = node.getNext();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        linkedList.addFirst(10);
        linkedList.addLast(20);
        linkedList.addLast(3);
        linkedList.addLast(2);
        linkedList.addLast(5);
        linkedList.print();
        System.out.println("��3���������Ԫ��");
        linkedList.add(3,100);
        linkedList.print();
        System.out.println("----------------------");
        System.out.println("ɾ������Ϊ5�Ľ��");
        linkedList.remove(5);
        linkedList.print();
        System.out.println("------------------");
        System.out.println("��ȡ����Ϊ2���±�����");
        System.out.println(linkedList.getData(2));
        System.out.println("------�������---------");
        linkedList.clear();
        linkedList.print();
    }
}
