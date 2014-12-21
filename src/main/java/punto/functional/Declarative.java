package punto.functional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import punto.util.Pair;

public class Declarative {
	

	public static <A, B> Stream<Pair<A,B>> zip(Stream<A> as, Stream<B> bs) {
	    Iterator<A> i1 = as.iterator();
	    Iterator<B> i2 = bs.iterator();
	    Iterable<Pair<A,B>> i=()->new Iterator<Pair<A,B>>() {
	        public boolean hasNext() {
	            return i1.hasNext() && i2.hasNext();
	        }
	        public Pair<A,B> next() {
	            return new Pair<A,B>(i1.next(), i2.next());
	        }
	    };
	    return StreamSupport.stream(i.spliterator(), false);
	}
	
	public static <A, B> Stream<Pair<A,B>> zip(A[] as, B[] bs) {
	    Iterator<A> i1 = Arrays.asList(as).iterator();
	    Iterator<B> i2 = Arrays.asList(bs).iterator();
	    Iterable<Pair<A,B>> i=()->new Iterator<Pair<A,B>>() {
	        public boolean hasNext() {
	            return i1.hasNext() && i2.hasNext();
	        }
	        public Pair<A,B> next() {
	            return new Pair<A,B>(i1.next(), i2.next());
	        }
	    };
	    return StreamSupport.stream(i.spliterator(), false);
	}
	
	public static <A, B> Stream<Pair<A,B>> zipLoose(Stream<A> as, Stream<B> bs) {
	    Iterator<A> i1 = as.iterator();
	    Iterator<B> i2 = bs.iterator();
	    Iterable<Pair<A,B>> i=()->new Iterator<Pair<A,B>>() {
	        public boolean hasNext() {
	            return i1.hasNext() || i2.hasNext();
	        }
	        public Pair<A,B> next() {
	        	return new Pair<A,B>(i1.hasNext()?i1.next():null, i2.hasNext()?i2.next():null);
	        }
	    };
	    return StreamSupport.stream(i.spliterator(), false);
	}
	
	public static <A, B> Stream<Pair<A,B>> zipLoose(A[] as, B[] bs) {
	    Iterator<A> i1 = Arrays.asList(as).iterator();
	    Iterator<B> i2 = Arrays.asList(bs).iterator();
	    Iterable<Pair<A,B>> i=()->new Iterator<Pair<A,B>>() {
	        public boolean hasNext() {
	            return i1.hasNext() || i2.hasNext();
	        }
	        public Pair<A,B> next() {
	            return new Pair<A,B>(i1.hasNext()?i1.next():null, i2.hasNext()?i2.next():null);
	        }
	    };
	    return StreamSupport.stream(i.spliterator(), false);
	}
	
	public static <T> Stream<T> upto(List<T> list, Function<T,Boolean> condition){
		for(int i = 0; i<list.size(); i++){
			if(condition.apply(list.get(i)))
				return list.subList(0, i).stream();
		}
		return list.stream();
	}
}
