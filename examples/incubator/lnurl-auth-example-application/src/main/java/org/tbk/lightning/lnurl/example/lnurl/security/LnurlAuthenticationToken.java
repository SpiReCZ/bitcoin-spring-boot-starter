package org.tbk.lightning.lnurl.example.lnurl.security;

import java.util.Arrays;
import java.util.Collection;

import fr.acinq.secp256k1.Hex;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;
import org.tbk.lnurl.K1;

import static java.util.Objects.requireNonNull;

public class LnurlAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final K1 k1;

	private final byte[] signature;

	private final byte[] linkingKey;

	public LnurlAuthenticationToken(K1 k1, byte[] signature, byte[] linkingKey) {
		super(null);
		this.k1 = requireNonNull(k1);
		this.signature = Arrays.copyOf(signature, signature.length);
		this.linkingKey = Arrays.copyOf(linkingKey, linkingKey.length);
		setAuthenticated(false);
	}

	public LnurlAuthenticationToken(K1 k1, byte[] signature, byte[] linkingKey,
									Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.k1 = requireNonNull(k1);
		this.signature = Arrays.copyOf(signature, signature.length);
		this.linkingKey = Arrays.copyOf(linkingKey, linkingKey.length);
		super.setAuthenticated(true); // must use super, as we override
	}

	@Override
	public Object getCredentials() {
		return this.k1.hex() + ":" + Hex.encode(this.signature);
	}

	@Override
	public Object getPrincipal() {
		return Hex.encode(this.linkingKey);
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		Assert.isTrue(!isAuthenticated,
				"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		super.setAuthenticated(false);
	}

	public K1 getK1() {
		return k1;
	}

	public byte[] getSignature() {
		return Arrays.copyOf(signature, signature.length);
	}

	public byte[] getLinkingKey() {
		return Arrays.copyOf(linkingKey, linkingKey.length);
	}
}