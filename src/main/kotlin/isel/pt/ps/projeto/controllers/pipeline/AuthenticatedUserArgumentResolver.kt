package isel.pt.ps.projeto.controllers.pipeline

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import isel.pt.ps.projeto.domain.users.AuthenticatedUser

@Component
class AuthenticatedUserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == AuthenticatedUser::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("HttpServletRequest is missing in the web request")
        return getUserFrom(request) ?: throw IllegalStateException("Authenticated user is not present in the request")
    }

    companion object {
        private const val KEY = "AuthenticatedUserArgumentResolver"

        fun addUserTo(user: AuthenticatedUser, request: HttpServletRequest) {
            return request.setAttribute(KEY, user)
        }

        fun getUserFrom(request: HttpServletRequest): AuthenticatedUser? {
            return request.getAttribute(KEY)?.let {
                it as? AuthenticatedUser
            }
        }
    }
}