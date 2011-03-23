/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, 2011 Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javapayload.handler.stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

public class TestStub extends StageHandler {
	
	public static int wait = 0;
	public boolean sendExit = false;
	
	public Class[] getNeededClasses() {
		return new Class[] { javapayload.stage.Stage.class, javapayload.stage.TestStub.class };
	}
	
	protected StageHandler createClone() {
		// do NOT clone sendExit!
		return new TestStub();
	}
	
	public void handleStreams(DataOutputStream out, InputStream in, String[] parameters) throws Exception {
		DataInputStream dis = new DataInputStream(in);
		byte[] indata = new byte[4096];
		dis.readFully(indata);
		Thread.sleep(wait);
		byte[] outdata = new byte[4096];
		Random r = new Random();
		r.nextBytes(outdata);
		out.write(outdata);
		out.flush();
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		byte[] indigest = digest.digest(indata);
		digest.reset();
		byte[] outdigest = digest.digest(outdata);
		out.write(indigest);
		out.writeBoolean(sendExit);
		out.flush();
		byte[] outdigest2 = new byte[outdigest.length];
		dis.readFully(outdigest2);
		if (!Arrays.equals(outdigest, outdigest2))
			throw new RuntimeException("Digests do not match");
		if (in.read() != -1)
			throw new RuntimeException("Stream not properly closed.");
		out.close();
	}
}
