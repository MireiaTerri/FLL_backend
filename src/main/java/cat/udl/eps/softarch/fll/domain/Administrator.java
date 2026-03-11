package cat.udl.eps.softarch.fll.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import java.util.Collection;

@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Administrator extends User {

	public static Administrator create(String id, String email, String password) {
		DomainValidation.requireNonBlank(id, "id");
		DomainValidation.requireValidEmail(email, "email");
		DomainValidation.requireNonBlank(password, "password");
		DomainValidation.requireLengthBetween(password, 8, 256, "password");

		Administrator admin = new Administrator();
		admin.setId(id);
		admin.setEmail(email);
		admin.setPassword(passwordEncoder.encode(password));
		return admin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");
	}
}
