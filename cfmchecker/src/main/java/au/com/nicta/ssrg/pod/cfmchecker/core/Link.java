package au.com.nicta.ssrg.pod.cfmchecker.core;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class Link {
	public class State {
		public State() {
			produced = 0;
			consumed = 0;
			missing = 0;
		}

		public Link getLink() {
			return Link.this;
		}

		public int getProduced() {
			return produced;
		}

		public int getConsumed() {
			return consumed;
		}

		public int getMissing() {
			return missing;
		}

		public int getRemaining() {
			return produced + missing - consumed;
		}

		public boolean hasRemaining() {
			return getRemaining() > 0;
		}

		public void produce() {
			++produced;
		}

		public void consume() {
			if (!hasRemaining()) {
				++missing;
			}
			++consumed;
		}

		@Override
		public String toString() {
			StringWriter writer = new StringWriter();
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator;
			try {
				generator = factory.createGenerator(writer);
				generator.writeStartObject();
				generator.writeFieldName("link");
				generator.writeRawValue(getLink().toString());
				generator.writeNumberField("produced", produced);
				generator.writeNumberField("consumed", consumed);
				generator.writeNumberField("missing", missing);
				generator.writeEndObject();
				generator.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return writer.toString();
		}

		private int produced;
		private int consumed;
		private int missing;
	}

	@JsonCreator
	public Link(
			@JsonProperty("source") Node source,
			@JsonProperty("target") Node target) {
		this.source = source;
		this.target = target;
	}

	public State newState() {
		return new State();
	}

	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator;
        try {
            generator = factory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeNumberField("sourceID", source.getID());
            generator.writeNumberField("targetID", target.getID());
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
	}

	private Node source;
	private Node target;
}
