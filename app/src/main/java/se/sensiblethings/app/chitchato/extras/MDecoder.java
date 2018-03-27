package se.sensiblethings.app.chitchato.extras;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class MDecoder {
	private CharsetDecoder decoder;

	public MDecoder(String encoding) {
		decoder = Charset.forName(encoding).newDecoder();
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		decoder.onMalformedInput(CodingErrorAction.REPORT);
	}

	public String decode(byte[] bytes) throws CharacterCodingException {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		CharBuffer cb = decoder.decode(bb);
		return cb.toString();
	}

	public boolean check(String input, String encoding)
			throws UnsupportedEncodingException {
		boolean _bool_ = false;
		//System.out.println("\ninput: [" + input + "]");
		byte[] bytes = input.getBytes(encoding);
		try {
			String output = decode(bytes);
			if (output.isEmpty()) {

				//System.out.println("~~~~~~~~~~~~~~NOT STRING ~~~~~~~~~~");
			}
			else {
				_bool_ = true;
				//System.out.println("~~~~~~~~~~~~~~ STRING ~~~~~~~~~~");
			}
			return _bool_;
		} catch (CharacterCodingException e) {
			//System.out.println("~~~~~~~~~~~~~~ NOT STRING ~~~~~~~~~~");
			return _bool_;
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		MDecoder md = new MDecoder("US-ASCII");

	}
}
